package pen.tools.draw;

import javafx.stage.Stage;
import pen.App;
import pen.tcl.TclEngineException;
import pen.tools.FXTool;
import pen.tools.ToolInfo;
import pen.apis.StencilExtension;
import pen.stencil.Stencil;
import pen.stencil.StencilBuffer;
import pen.tcl.TclEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Deque;

/**
 * Application class for the "pen draw" tool.
 */
public class DrawTool extends FXTool {
    /**
     * Tool information for this tool, for use by the launcher.
     */
    public static final ToolInfo INFO = new ToolInfo(
        "draw",
        "drawing.tcl",
        "Saves a pen drawing as a PNG file.",
        """
Given a Pen drawing script, outputs the drawing as a
PNG file.""",
        DrawTool::main
    );

    //------------------------------------------------------------------------
    // Instance Variables

    private File drawingFile;
    private String script;
    private final StencilBuffer buffer = new StencilBuffer();

    //------------------------------------------------------------------------
    // Main-line code

    /**
     * Creates the tool's application object.
     */
    public DrawTool() {
        super(INFO);
    }

    @Override
    public void run(Stage stage, Deque<String> argq) {
        // FIRST, parse the command line arguments.
        argq.poll(); // Skip the tool name

        if (argq.size() != 1) {
            App.showUsage(INFO);
            System.exit(1);
        }

        drawingFile = new File(argq.poll());

        try {
            script = Files.readString(drawingFile.toPath());
        } catch (IOException ex) {
            script = null;
            System.out.println("Could not read file: " + drawingFile);
            System.exit(1);
        }

        buffer.draw(this::drawDrawing);

        try {
            var outFile = asPNGFile(drawingFile);
            System.out.println("Writing: " + outFile);
            buffer.save(asPNGFile(drawingFile));
        } catch (IOException ex) {
            System.out.println("*** Failed to write file: " + ex.getMessage());
        }

        System.exit(0); // Because JavaFX.
    }

    private void drawDrawing(Stencil stencil) {
        stencil.clear();
        var engine = new TclEngine();
        // TODO Need better installation story
        var stencilExtension = new StencilExtension(engine, stencil);

        try {
            engine.eval(script);
        } catch (TclEngineException ex) {
            System.out.println("Drawing error at line " + ex.getErrorLine() +
                " of " + drawingFile + ":\n" + ex.getErrorInfo());
            System.exit(1);
        }
    }

    /**
     * Given a file name, removes the file type and replaces it with ".png"
     * @param file The input file name
     * @return The PNG file name
     */
    private File asPNGFile(File file) {
        var text = file.toString();
        var ndx = text.lastIndexOf('.');
        if (ndx != -1) {
            text = text.substring(0, ndx);
        }
        return new File(text + ".png");
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
