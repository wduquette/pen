package pen.tools.history;

import javafx.stage.Stage;
import pen.App;
import pen.DataFileException;
import pen.DataFiles;
import pen.HistoryFile;
import pen.history.Cap;
import pen.history.History;
import pen.history.Entity;
import pen.history.Incident;
import pen.tools.FXTool;
import pen.tools.ToolInfo;
import pen.util.TextAlign;
import pen.util.TextColumn;
import pen.util.TextTable;

import java.io.File;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

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

--result timeline|entity|incident|type|summary

  timeline    Output a timeline chart
  entity      Output a table of entities
  incident    Output a table of incidents
  type        Output a table of entity types
  summary     Output a summary of the history content
  
--format terminal|markdown

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
                case "--result", "-r" ->
                    options.results.add(toEnum(Result.class, opt, argq));
                case "--format", "-f" ->
                    options.mode = toEnum(TextTable.Mode.class, opt, argq);
                case "--debug" -> options.debug = true;
                default -> throw unknownOption(opt);
            }
        }

        // NEXT, try to open the file
        HistoryFile historyFile;

        try {
            historyFile = DataFiles.loadHistory(historyFilePath);
        } catch (DataFileException ex) {
            if (options.debug) {
                println(ex.getMessage());
                println(ex.getDetails());
            }
            throw error("Failed to read history file", ex);
        }

        var history = historyFile.history();
        var query = historyFile.query();
        view = query.execute(history);

        if (options.results.isEmpty()) {
            options.results.add(Result.TIMELINE);
        }

        if (options.results.contains(Result.TYPE)) {
            var types = view.getEntityMap().values().stream()
                .map(Entity::type)
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));

            println("Types: " + (types.isEmpty() ? "None defined." : types));
        }

        if (options.results.contains(Result.ENTITY)) {
            printTable(getSortedEntities(), ENTITIES);
        }

        if (options.results.contains(Result.INCIDENT)) {
            printTable(view.getIncidents(), INCIDENTS);
        }

        if (options.results.contains(Result.TIMELINE)) {
            println(view.toTimelineChart());
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

    private List<Entity> getSortedEntities() {
        return view.getEntityMap().values().stream()
            .sorted(Comparator.comparing(Entity::id))
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
        new TextColumn<>("Name", TextAlign.LEFT, Entity::name),
        new TextColumn<>("Type", TextAlign.LEFT, Entity::type),
        new TextColumn<>("Frame", TextAlign.LEFT, this::entityTimeFrame)
    ));

    private String entityTimeFrame(Entity e) {
        var p = view.getPeriods().get(e.id());

        if (p == null) {
            return "No incidents";
        }

        return
            (p.startCap() == Cap.HARD ? "[" : "(") +
            view.formatMoment(p.start()) +
            " to " +
            view.formatMoment(p.end()) +
            (p.endCap() == Cap.HARD ? ")" : "]")
            ;
    }

    public final TextTable<Incident> INCIDENTS = new TextTable<>(List.of(
        new TextColumn<>("Moment", TextAlign.RIGHT,
            row -> view().formatMoment(row.moment())),
        new TextColumn<>("Incident", TextAlign.LEFT, Incident::label),
        new TextColumn<>("Concerns", TextAlign.LEFT,
            row -> String.join(", ", row.entityIds()))
    ));

    //------------------------------------------------------------------------
    // Options Structure

    private static class Options {
        TextTable.Mode mode = TextTable.Mode.TERMINAL;
        LinkedHashSet<Result> results = new LinkedHashSet<>();
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
