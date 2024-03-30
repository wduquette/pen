package pen.tools.history;

import javafx.stage.Stage;
import pen.App;
import pen.DataFileException;
import pen.DataFiles;
import pen.HistoryFile;
import pen.history.History;
import pen.history.Incident;
import pen.tools.FXTool;
import pen.tools.ToolInfo;
import pen.util.TextAlign;
import pen.util.TextColumn;
import pen.util.TextTable;

import java.io.File;
import java.util.Deque;
import java.util.List;

/**
 * Application class for the "pen draw" tool.
 */
public class HistoryTool extends FXTool {
    /**
     * Tool information for this tool, for use by the launcher.
     */
    public static final ToolInfo INFO = new ToolInfo(
        "history",
        "history.hist",
        "Validates and queries history files.",
        """
Given a .hist History file, outputs the file as a text timeline chart.
""",
        HistoryTool::main
    );

    //------------------------------------------------------------------------
    // Instance Variables

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
        if (argq.size() != 1) {
            printUsage(App.NAME);
            exit(1);
        }
        assert !argq.isEmpty();

        var historyFilePath = new File(argq.poll()).toPath();

        // NEXT, try to open the file
        HistoryFile historyFile;

        try {
            historyFile = DataFiles.loadHistory(historyFilePath);
        } catch (DataFileException ex) {
//            println(ex.getMessage());
//            println(ex.getDetails());
            throw error("Failed to read history file", ex);
        }

        var history = historyFile.history();
        var query = historyFile.query();
        view = query.execute(history);
        println(INCIDENTS.toMarkdown(view.getIncidents()));
        println(INCIDENTS.toTerminal(view.getIncidents()));
        println(view.toTimelineChart());

        exit(); // Because JavaFX.
    }

    private History view() {
        return view;
    }

    public final TextTable<Incident> INCIDENTS = new TextTable<>(List.of(
        new TextColumn<>("Moment", TextAlign.RIGHT,
            row -> view().formatMoment(row.moment())),
        new TextColumn<>("Incident", TextAlign.LEFT, Incident::label),
        new TextColumn<>("Concerns", TextAlign.LEFT,
            row -> String.join(", ", row.entityIds()))
    ));

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
