package pen.tools.run;

import com.wjduquette.joe.Joe;
import com.wjduquette.joe.JoeError;
import com.wjduquette.joe.SyntaxError;
import com.wjduquette.joe.console.ConsolePackage;
import com.wjduquette.joe.tools.Tool;
import com.wjduquette.joe.tools.ToolInfo;
import pen.App;
import pen.joe.text.JoeTextPackage;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;

/**
 * The implementation for the {@code pen run} tool.
 */
public class RunTool implements Tool {
    /**
     * Tool information for this tool, for use by the launcher.
     */
    public static final ToolInfo INFO = new ToolInfo(
        "run",
        "file.joe",
        "Executes a Joe script.",
        """
        Executes the Joe script.  Pen's non-GUI Joe packages are loaded
        automatically.
        """,
        RunTool::main
    );

    //-------------------------------------------------------------------------
    // Constructor

    /** Creates the tool. */
    public RunTool() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // Execution

    /**
     * Gets implementation info about the tool.
     * @return The info.
     */
    public ToolInfo toolInfo() {
        return INFO;
    }

    private void run(String[] args) {
        var argq = new ArrayDeque<>(List.of(args));

        if (argq.isEmpty()) {
            printUsage(App.NAME);
            System.exit(1);
        }

        var joe = new Joe();
        joe.installPackage(JoeTextPackage.PACKAGE);

        var path = argq.poll();

        var consolePackage = new ConsolePackage();
        consolePackage.setScript(path);
        consolePackage.getArgs().addAll(argq);
        joe.installPackage(consolePackage);



        try {
            joe.runFile(path);
        } catch (IOException ex) {
            System.err.println("Could not read script: " + path +
                "\n*** " + ex.getMessage());
            exit(1);
        } catch (SyntaxError ex) {
            System.err.println(ex.getErrorReport());
            System.err.println("*** " + ex.getMessage());
            exit(1);
        } catch (JoeError ex) {
            System.err.print("*** Error in script: ");
            System.err.println(ex.getJoeStackTrace());
            exit(1);
        }
    }


    //-------------------------------------------------------------------------
    // Main

    /**
     * The tool's main routine.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        new RunTool().run(args);
    }
}
