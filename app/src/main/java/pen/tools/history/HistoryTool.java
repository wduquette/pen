package pen.tools.history;

import javafx.stage.Stage;
import pen.App;
import pen.DataFileException;
import pen.DataFiles;
import pen.history.History;
import pen.tools.FXTool;
import pen.tools.ToolInfo;

import java.io.File;
import java.util.Deque;

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

        var historyFile = new File(argq.poll()).toPath();

        // NEXT, try to open the file
        History history;
        try {
            history = DataFiles.loadHistory(historyFile);
        } catch (DataFileException ex) {
            println(ex.getMessage());
            println(ex.getDetails());
            throw error("Failed to read history file", ex);
        }

        println(history.toTimelineChart());

        exit(); // Because JavaFX.
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
