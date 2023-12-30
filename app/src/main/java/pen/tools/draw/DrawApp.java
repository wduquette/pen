package pen.tools.draw;

import javafx.application.Application;
import javafx.stage.Stage;
import pen.apis.StencilExtension;
import pen.stencil.Stencil;
import pen.stencil.StencilBuffer;
import pen.tcl.TclEngine;
import tcl.lang.TclException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;

public class DrawApp extends Application {
    //------------------------------------------------------------------------
    // Instance Variables

    private File drawingFile;
    private String script;
    private StencilBuffer buffer = new StencilBuffer();

    //------------------------------------------------------------------------
    // Main-line code

    @Override
    public void start(Stage stage) {
        // FIRST, parse the command line arguments.
        var argq = new ArrayDeque<>(getParameters().getRaw());
        argq.poll(); // Skip the tool name

        if (argq.size() != 1) {
            // TODO: need a MUCH better usage solution.
            System.out.println("Usage: pen draw drawing.tcl");
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
        } catch (TclException ex) {
            // TODO: Do better
            System.out.println("Drawing error: " +
                engine.interp().getResult().toString());
            System.exit(1);
        }
    }

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

    public static void main(String[] args) {
        launch(args);
    }
}
