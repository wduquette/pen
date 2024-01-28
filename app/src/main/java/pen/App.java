/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package pen;

import pen.tools.ToolException;
import pen.tools.ToolInfo;
import pen.tools.demo.DemoTool;
import pen.tools.draw.DrawTool;
import pen.tools.view.ViewTool;

import java.util.*;

/**
 * The Pen application launcher.  Launches various tool applications, provides
 * help, etc.
 */
public class App {
    /** The application name used in console output. */
    public static final String NAME = "pen";

    //-------------------------------------------------------------------------
    // Instance variables

    // Array of tool applications that can be launched.
    private final static Map<String, ToolInfo> TOOLS = new TreeMap<>();

    static {
        TOOLS.putAll(Map.of(
            DemoTool.INFO.name(), DemoTool.INFO,
            DrawTool.INFO.name(), DrawTool.INFO,
            ViewTool.INFO.name(), ViewTool.INFO
        ));
    }

    //-------------------------------------------------------------------------
    // Launcher App

    /**
     * Initializes the application object.
     */
    public App() {
        super();
    }

    /**
     * Gets the desired tool and executes it
     * @param args The arguments from the command line.
     */
    public void app(String[] args) {
        var argq = new ArrayDeque<>(List.of(args));

        if (argq.isEmpty()) {
            println("Usage: pen <tool> [<arguments...>]");
            println("");
            println("Run \"pen help\" for a list of tools.");
            System.exit(1);
        }

        var subcommand = argq.poll();

        var tool = TOOLS.get(subcommand);

        if (tool != null) {
            tool.launcher().accept(rest(args));
        } else if (subcommand.equals("help")) {
            showHelp(argq);
        } else {
            showFailure(subcommand);
        }
    }

    private String[] rest(String[] args) {
        var rest = new String[args.length - 1];

        for (int i = 1; i < args.length; i++) {
            rest[i-1] = args[i];
        }

        return rest;
    }

    private void showFailure(String subcommand) {
        println("Error, unrecognized tool: \"" + subcommand + "\"");
        println("");
        println("Run \"pen help\" for a list of subcommands.");
    }

    private void showHelp(Deque<String> argq) {
        if (argq.isEmpty()) {
            System.out.println();
            System.out.println("Pen supports the following tools:\n");

            for (var tool : TOOLS.values()) {
                System.out.printf("%-8s %s\n", tool.name(), tool.oneLiner());
            }

            System.out.println("\nEnter \"pen help <tool>\" for help on a tool.");
            System.out.println();
        } else {
            var subcommand = argq.poll();
            var tool = TOOLS.get(subcommand);

            if (tool != null) {
                tool.printHelp(NAME);
            } else {
                showFailure(subcommand);
            }
        }
    }

    //-------------------------------------------------------------------------
    // Helpers

    /**
     * Print to System.out.
     * @param text Text to print.
     */
    public void println(String text) {
        System.out.println(text);
    }

    //-------------------------------------------------------------------------
    // Main

    /**
     * App's main routine.  Creates a new application object and passes it the
     * arguments; handles errors thrown by the application.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        try {
            new App().app(args);
        } catch (ToolException ex) {
            System.out.println("Error: " + ex.getMessage());
//            ex.printStackTrace(System.out);
        } catch (Exception ex) {
            System.out.println("Unexpected Exception Thrown: " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
