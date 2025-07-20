package pen.tools.history;

import com.wjduquette.joe.tools.FXTool;
import com.wjduquette.joe.tools.ToolInfo;
import javafx.stage.Stage;
import pen.App;
import pen.DataFileException;
import pen.DataFiles;
import pen.HistoryFile;
import pen.calendars.Calendar;
import pen.history.History;
import pen.history.Entity;
import pen.history.EntityType;
import pen.history.HistoryQuery;
import pen.history.Incident;
import pen.util.TextAlign;
import pen.util.TextColumn;
import pen.util.TextTable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static pen.util.TextTable.Mode.MARKDOWN;
import static pen.util.TextTable.Mode.TERMINAL;

/**
 * Application class for the "pen draw" tool.
 */
public class HistoryTool extends FXTool {
    /**
     * Tool information for this tool, for use by the launcher.
     */
    public static final ToolInfo INFO = new ToolInfo(
        "history",
        "history.hist [options...]",
        "Validates and queries history files.",
        """
Given a .hist History file, queries and outputs data from the file.  By
default it outputs a brief summary of the history.  The options are
as follows:

--output timeline|entity|incident|type|summary
-o timeline|entity|incident|type|summary

  timeline    Output a timeline chart
  entity      Output a table of entities
  incident    Output a table of incidents
  type        Output a table of entity types
  summary     Output a summary of the history content
  
--format terminal|markdown
-f terminal|markdown

  terminal    Output a table in an attractive form for the terminal
  markdown    Output a markdown table

--entity entityId, -e entityId

  Include the given entity in the output.

--type type, -t type

  Include the given entity type in the output.
  
--start moment
--end moment
   
   Include only incidents whose moments are between the start and the end,
   inclusive.  If the history specifies a calendar, the moments are specified
   in the calendar date format.

--anniversaries

   If given, anniversaries of memorial and birthday incidents will be added
   to the incident list.
   
--debug

   Enable verbose debugging output.
""",
        HistoryTool::main
    );

    //------------------------------------------------------------------------
    // Instance Variables

    // The options
    private final Options options = new Options();

    // The loaded history
    private History view;


    //------------------------------------------------------------------------
    // Main-line code

    /**
     * Creates the tool's application object.
     */
    public HistoryTool() {
        super(INFO);
    }

    @Override
    public void run(Stage stage, Deque<String> argq) {
        // FIRST, parse the command line arguments.
        if (argq.isEmpty()) {
            printUsage(App.NAME);
            exit(1);
        }
        assert !argq.isEmpty();

        var historyFilePath = new File(argq.poll()).toPath();

        while (!argq.isEmpty()) {
            var opt = argq.poll();
            switch (opt) {
                case "--entity", "-e" ->
                    options.includedEntities.add(toOptArg(opt, argq));
                case "--output", "-o" ->
                    options.results.add(toEnum(Result.class, opt, argq));
                case "--format", "-f" ->
                    options.mode = toEnum(TextTable.Mode.class, opt, argq);
                case "--start" -> options.start = argq.poll();
                case "--end" -> options.end = argq.poll();
                case "--anniversaries" -> options.anniversaries = true;
                case "--debug" -> options.debug = true;
                default -> throw unknownOption(opt);
            }
        }

        // NEXT, try to open the file
        HistoryFile historyFile = null;

        try {
            historyFile = DataFiles.loadHistory(historyFilePath);
        } catch (DataFileException ex) {
            println("Failed to read history file: " + ex.getMessage());
            println(ex.getDetails());
            System.exit(1);
        }

        var history = historyFile.history();
        var calendar = historyFile.getPrimaryCalendar();

        var query = new HistoryQuery();

        if (calendar != null && options.anniversaries) {
            query.expandAnniversaries(historyFile.getPrimaryCalendar());
        }

        if (!options.includedEntities.isEmpty()) {
            query.includes(options.includedEntities);
        }

        if (options.start != null) {
            query.noEarlierThan(toMoment(calendar, options.start));
        }

        if (options.end != null) {
            query.noLaterThan(toMoment(calendar, options.end));
        }

        view = query.execute(history);

        if (view.getIncidents().isEmpty()) {
            println("No incidents found.");
            exit();
        }

        if (options.results.isEmpty()) {
            options.results.add(Result.TIMELINE);
        }

        if (options.results.contains(Result.TYPE)) {
            printTable(getSortedTypes(), ENTITY_TYPES);
        }

        if (options.results.contains(Result.ENTITY)) {
            if (options.mode == TERMINAL) {
                printTable(getSortedEntities(), ENTITIES);
            } else {
                printTable(getSortedEntities(), ENTITIES_MARKDOWN);
            }
        }

        if (options.results.contains(Result.INCIDENT)) {
            if (options.mode == TERMINAL) {
                printTable(view.getIncidents(), INCIDENTS);
            } else {
                printTable(view.getIncidents(), INCIDENTS_MARKDOWN);
            }
        }

        if (options.results.contains(Result.TIMELINE)) {
            if (options.mode == MARKDOWN) {
                println("```text");
            }

            println(view.toTimelineChart());

            if (options.mode == MARKDOWN) {
                println("```");
            }
        }

        if (options.results.contains(Result.SUMMARY)) {
            println("Entities:   " + view.getEntityMap().size());
            println("Incidents:  " + view.getIncidents().size());

            var frame = view.getTimeFrame();

            println("Time Range: " +
                view.formatMoment(frame.start()) +
                " to " +
                view.formatMoment(frame.end()));
        }

        exit(); // Because JavaFX.
    }

    private int toMoment(Calendar cal, String momentString) {
        try {
            if (cal == null) {
                return Integer.parseInt(momentString);
            } else {
                return cal.parse(momentString);
            }
        } catch (Exception ex) {
            throw error("Could not make sense of timestamp: \"" +
                    momentString + "\"");
        }
    }

    private List<Entity> getSortedEntities() {
        var list = new ArrayList<Entity>();

        view.getEntityMap().values().stream()
            .filter(Entity::prime)
            .sorted(Comparator.comparing(Entity::id))
            .forEach(list::add);
        view.getEntityMap().values().stream()
            .filter(e -> !e.prime())
            .sorted(Comparator.comparing(Entity::id))
            .forEach(list::add);

        return list;
    }

    private List<EntityType> getSortedTypes() {
        return view.getTypeMap().values().stream()
            .sorted(Comparator.comparing(EntityType::id))
            .toList();
    }

    private <R> void printTable(List<R> rows, TextTable<R> format) {
        println(format.toTable(rows, options.mode));
    }

    private History view() {
        return view;
    }

    public final TextTable<Entity> ENTITIES = new TextTable<>(List.of(
        new TextColumn<>("ID", TextAlign.LEFT, Entity::id),
        new TextColumn<>("Type", TextAlign.LEFT, Entity::type),
        new TextColumn<>("Name", TextAlign.LEFT, Entity::name),
        new TextColumn<>("Prime", TextAlign.LEFT, e -> String.valueOf(e.prime()))
    ));

    public final TextTable<Entity> ENTITIES_MARKDOWN = new TextTable<>(List.of(
        new TextColumn<>("ID", TextAlign.LEFT, Entity::id),
        new TextColumn<>("Type", TextAlign.LEFT, Entity::type),
        new TextColumn<>("Name", TextAlign.LEFT, this::entityLink),
        new TextColumn<>("Prime", TextAlign.LEFT, e -> String.valueOf(e.prime()))
    ));

    private String entityLink(Entity e) {
        return "[[" + e.name() + "]]";
    }

    public final TextTable<EntityType> ENTITY_TYPES = new TextTable<>(List.of(
        new TextColumn<>("ID", TextAlign.LEFT, EntityType::id),
        new TextColumn<>("Name", TextAlign.LEFT, EntityType::name),
        new TextColumn<>("Prime", TextAlign.LEFT, t -> String.valueOf(t.prime()))
    ));

    public final TextTable<Incident> INCIDENTS = new TextTable<>(List.of(
        new TextColumn<>("Moment", TextAlign.RIGHT,
            row -> view().formatMoment(row.moment())),
        new TextColumn<>("Incident", TextAlign.LEFT, Incident::label),
        new TextColumn<>("Concerns", TextAlign.LEFT,
            row -> String.join(", ", row.entityIds()))
    ));

    public final TextTable<Incident> INCIDENTS_MARKDOWN = new TextTable<>(List.of(
        new TextColumn<>("Moment", TextAlign.RIGHT,
            row -> view().formatMoment(row.moment())),
        new TextColumn<>("Incident", TextAlign.LEFT, Incident::label),
        new TextColumn<>("Concerns", TextAlign.LEFT, this::entityLinks)
    ));

    private String entityLinks(Incident incident) {
        return incident.entityIds().stream()
            .map(id -> view.getEntityMap().get(id))
            .map(this::entityLink)
            .collect(Collectors.joining(", "));
    }

    //------------------------------------------------------------------------
    // Options Structure

    private static class Options {
        TextTable.Mode mode = TERMINAL;
        LinkedHashSet<Result> results = new LinkedHashSet<>();
        List<String> includedEntities = new ArrayList<>();
        String start;
        String end;
        boolean anniversaries = false;
        boolean debug = false;
    }

    private enum Result {
        TIMELINE,
        ENTITY,
        INCIDENT,
        TYPE,
        SUMMARY
    }

    //------------------------------------------------------------------------
    // Main

    /**
     * The tool's JavaFX Application main() method.  Launches the application.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
