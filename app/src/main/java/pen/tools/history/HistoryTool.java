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

--timeline, -t      Output a timeline chart
--entities, -e      Output a table of the entities
--incidents, -i     Output a table of the incidents
--types             Output a table of the entity types.
--markdown          Tables will be formatted as markdown.
--debug             Enable verbose debugging output.
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
                case "--debug" -> options.debug = true;
                case "--timeline", "-t" -> options.showTimeline = true;
                case "--entities", "-e" -> options.showEntities = true;
                case "--incidents", "-i" -> options.showIncidents = true;
                case "--types", "-y" -> options.showEntityTypes = true;
                case "--markdown" -> options.mode = TextTable.Mode.MARKDOWN;
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

        if (options.showEntityTypes) {
            options.showSummary = false;
            var types = view.getEntityMap().values().stream()
                .map(Entity::type)
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));

            println("Types: " + (types.isEmpty() ? "None defined." : types));
        }

        if (options.showEntities)    {
            options.showSummary = false;
            printTable(getSortedEntities(), ENTITIES);
        }

        if (options.showIncidents)   {
            options.showSummary = false;
            printTable(view.getIncidents(), INCIDENTS);
        }

        if (options.showTimeline) {
            options.showSummary = false;
            println(view.toTimelineChart());
        }

        if (options.showSummary) {
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
        boolean showSummary = true;
        boolean showTimeline = false;
        boolean showEntities = false;
        boolean showIncidents = false;
        boolean showEntityTypes = false;
        boolean debug = false;
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
