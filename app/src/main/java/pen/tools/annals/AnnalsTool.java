package pen.tools.annals;

import javafx.scene.Scene;
import javafx.stage.Stage;
import pen.*;
import pen.tools.FXTool;
import pen.tools.ToolException;
import pen.tools.ToolInfo;

import java.io.File;
import java.util.*;

@SuppressWarnings("FieldCanBeLocal")
public class AnnalsTool extends FXTool {
    public static final ToolInfo INFO = new ToolInfo(
        "annals",
        "datafile.cal | datafile.hist",
        "Displays fictional calendars and histories.",
        """
            Given a calendar file or a history file, this tool will display the
            defined calendars, populated with any relevant dates.  The user
            may choose which calendar to view, and do date conversions.
            """,
        AnnalsTool::main
    );

    //------------------------------------------------------------------------
    // Instance Variables

    private MainView main;

    //------------------------------------------------------------------------
    // Constructor

    public AnnalsTool() {
        super(INFO);
    }

    //------------------------------------------------------------------------
    // Main-line code

    @Override
    public void run(Stage stage, Deque<String> argq) {
        // FIRST, parse the command line arguments.
        if (argq.size() != 1) {
            printUsage(App.NAME);
            exit(1);
        }
        assert !argq.isEmpty();

        var dataPath = new File(argq.poll()).toPath();

        if (!dataPath.toString().endsWith(".cal") &&
            !dataPath.toString().endsWith(".hist")
        ) {
            throw new ToolException("Unrecognized file type: " + dataPath);
        }

        // NEXT, build the GUI
        main = new MainView(this);

        // NEXT, pop up the window
        Scene scene = new Scene(main, 1000, 800);

        stage.setTitle("pen calendar - " +
            dataPath.getFileName());
        stage.setScene(scene);
        stage.show();

        main.setDataPath(dataPath);
    }

    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
