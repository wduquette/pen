package pen.tools.draw;

import com.wjduquette.joe.tools.FXTool;
import com.wjduquette.joe.tools.ToolInfo;
import javafx.stage.Stage;
import pen.App;
import pen.tcl.TclEngineException;
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
        PNG file.
        """,
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
        if (argq.size() != 1) {
            printUsage(App.NAME);
            exit(1);
        }

        assert !argq.isEmpty();
        drawingFile = new File(argq.poll());

        try {
            script = Files.readString(drawingFile.toPath());
        } catch (IOException ex) {
            script = null;
            throw error("Could not read file: " + drawingFile, ex);
        }

        buffer.draw(this::drawDrawing);

        try {
            var outFile = asPNGFile(drawingFile);
            println("Writing: " + outFile);
            buffer.save(asPNGFile(drawingFile));
        } catch (IOException ex) {
            throw error("Failed to write file: " + ex.getMessage(), ex);
        }

        exit(); // Because JavaFX.
    }

    private void drawDrawing(Stencil stencil) {
        stencil.clear();
        var engine = new TclEngine();
        engine.install(new StencilExtension(stencil));

        try {
            engine.eval(script);
        } catch (TclEngineException ex) {
            throw error("Drawing error at line " + ex.getErrorLine() +
                " of " + drawingFile + ":\n" + ex.getErrorInfo());
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
