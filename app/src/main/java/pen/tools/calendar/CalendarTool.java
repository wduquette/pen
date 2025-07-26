package pen.tools.calendar;

import com.wjduquette.joe.Joe;
import com.wjduquette.joe.JoeError;
import com.wjduquette.joe.Keyword;
import com.wjduquette.joe.SourceBuffer;
import com.wjduquette.joe.nero.Fact;
import com.wjduquette.joe.nero.FactSet;
import com.wjduquette.joe.nero.Nero;
import com.wjduquette.joe.tools.Tool;
import com.wjduquette.joe.tools.ToolInfo;
import pen.*;
import pen.calendars.*;
import pen.calendars.Calendar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Application class for the "pen calendar" tool.
 */
public class CalendarTool implements Tool {
    /**
     * Tool information for this tool, for use by the launcher.
     */
    public static final ToolInfo INFO = new ToolInfo(
        "calendar",
        "calendar.nero",
        "Formats calendars based on calendar data in Nero format.",
        """
Given a .nero file of calendar data, queries and outputs data from the file,
formatted for use.
""",
        CalendarTool::main
    );

    //------------------------------------------------------------------------
    // Instance Variables

    // None yet

    //------------------------------------------------------------------------
    // Main-line code

    /**
     * Creates the tool's application object.
     */
    public CalendarTool() {
        // Nothing to do.
    }

    //------------------------------------------------------------------------
    // Execution

    /**
     * Gets implementation info about the tool.
     * @return The info.
     */
    public ToolInfo toolInfo() {
        return INFO;
    }

    public void run(String[] args) {
        var argq = new ArrayDeque<>(List.of(args));

        // FIRST, parse the command line arguments.
        if (argq.isEmpty()) {
            printUsage(App.NAME);
            exit(1);
        }

        assert !argq.isEmpty();
        var path = new File(argq.poll()).toPath();


        try {
            CalendarFile calendarFile = load(path);
            // TODO: Support specifying default calendar and "today" in the
            // the Nero input
            println("Got calendars: " + calendarFile.getNames());
        } catch (DataFileException ex) {
            println("Failed to read calendar file: " + ex.getMessage());
            println(ex.getDetails());
            System.exit(1);
        }
    }

    private CalendarFile load(Path path) throws DataFileException {
        try {
            var script = Files.readString(path);
            var source = new SourceBuffer(path.getFileName().toString(), script);
            var joe = new Joe();
            var nero = new Nero(joe);
            var db = nero.execute(source).getKnownFacts();
            var cals = new LinkedHashMap<String, Calendar>();

            for (var cal : db.getRelation("Calendar")) {
                var id = joe.toKeyword(cal.get("id"));
                cals.put(id.name(), readCalendar(joe, cal, db));
            }
            return new CalendarFile(path, cals, 0);
        } catch (Exception ex) {
            throw error("calendar", ex);
        }
    }

    private Calendar readCalendar(Joe joe, Fact cal, FactSet db) {
        var id = joe.toKeyword(cal.get("id"));
        return new BasicCalendar.Builder()
            .epochOffset(joe.toInteger(cal.get("offset")))
            .era(readEra(joe, "Era", id, db))
            .priorEra(readEra(joe, "PriorEra", id, db))
            .week(readWeek(joe, id, db))
            .months(readMonths(joe, id, db))
            .build();
    }

    private Era readEra(Joe joe, String relation, Keyword id, FactSet db) {
        var era = readOne(relation, id, db);
        var shortForm = joe.stringify(era.get("short"));
        var fullForm = joe.stringify(era.get("full"));
        return new Era(shortForm, fullForm);
    }

    private Week readWeek(Joe joe, Keyword id, FactSet db) {
        var week = readOne("Week", id, db);
        var offset = joe.toInteger(week.get("offset"));

        var days = readSeq(joe, "Weekday", id, db);
        var list = new ArrayList<Weekday>();
        for (var day : days) {
            list.add(new Weekday(
                joe.stringify(day.get("full")),
                joe.stringify(day.get("short")),
                joe.stringify(day.get("unambiguous")),
                joe.stringify(day.get("tiny"))
            ));
        }
        return new Week(list, offset);
    }

    private List<BoundedMonth> readMonths(Joe joe, Keyword id, FactSet db) {
        // TODO: Support variable length months
        var months = new ArrayList<BoundedMonth>();
        for (var item : readSeq(joe, "Month", id, db)) {
            int days = joe.toInteger(item.get("days"));
            months.add(new BoundedMonth(
                joe.stringify(item.get("full")),
                joe.stringify(item.get("short")),
                joe.stringify(item.get("unambiguous")),
                joe.stringify(item.get("tiny")),
                y -> days
            ));
        }

        return months;
    }

    // TODO: Make these standard FactSet queries?
    private Fact readOne(String relation, Keyword id, FactSet db) {
        var facts = db.getRelation(relation).stream()
            .filter(f -> f.get("calendar").equals(id))
            .toList();
        if (facts.isEmpty()) throw new JoeError(
            "No " + relation + " found for calendar '" + id + "'.");
        if (facts.size() > 1) throw new JoeError(
            "Too many " + relation + "s found for calendar '" + id + "'.");

        return facts.getFirst();
    }

    // Reads a sequence of items
    private List<Fact> readSeq(Joe joe, String relation, Keyword id, FactSet db) {
        // TODO: Check for duplicate sequence numbers
        return db.getRelation(relation).stream()
            .filter(f -> f.get("calendar").equals(id))
            .sorted(Comparator.comparing(f -> seq(joe, f)))
            .toList();
    }

    int seq(Joe joe, Fact fact) {
        return joe.toInteger(fact.get("seq"));
    }

    @SuppressWarnings("SameParameterValue")
    private static DataFileException error(String what, Exception ex) {
        return switch (ex) {
            case IOException ignored ->
                new DataFileException(
                    "Error reading " + what + ", " +ex. getMessage(), ex);
            case JoeError ignored ->
                new DataFileException(
                    "Error in " + what + ", " + ex.getMessage(), ex);
            default ->
                new DataFileException(
                    "Unexpected error while loading " + what + ", " +
                        ex.getMessage(), ex);
        };
    }

    //------------------------------------------------------------------------
    // Main

    /**
     * The tool's JavaFX Application main() method.  Launches the application.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        new CalendarTool().run(args);
    }
}
