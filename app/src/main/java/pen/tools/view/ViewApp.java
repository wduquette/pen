package pen.tools.view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pen.App;
import pen.apis.StencilExtension;
import pen.stencil.Pen;
import pen.stencil.Stencil;
import pen.stencil.StencilBuffer;
import pen.stencil.StencilDrawing;
import pen.tcl.TclEngine;
import tcl.lang.TclException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;

import static pen.stencil.Stencil.label;
import static pen.stencil.Stencil.rect;

public class ViewApp extends Application {
    //------------------------------------------------------------------------
    // Instance Variables

    private final StackPane root = new StackPane();
    private final Canvas canvas = new Canvas();
    private Stencil stencil;
    private File drawingFile;

    //------------------------------------------------------------------------
    // Main-line code

    @Override
    public void start(Stage stage) {
        // FIRST, parse the command line arguments.
        var argq = new ArrayDeque<>(getParameters().getRaw());
        argq.poll(); // Skip the tool name

        if (argq.size() != 1) {
            // TODO: need a MUCH better usage solution.
            System.out.println("Usage: pen view drawing.tcl");
            System.exit(1);
        }

        drawingFile = new File(argq.poll());

        // NEXT, set up the GUI
        root.getChildren().add(canvas);
        stencil = new Stencil(canvas.getGraphicsContext2D());
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        Scene scene = new Scene(root, 400, 400);

        stage.setTitle("pen view " + drawingFile);
        stage.setScene(scene);
        stage.show();

        // NEXT, repaint on window size change, and on user request.
        canvas.widthProperty().addListener((p,o,n) -> repaint());
        canvas.heightProperty().addListener((p,o,n) -> repaint());
        // TODO: Add Shortcut+R listener

        repaint();
    }

    private void repaint() {
        stencil.clear();
        var engine = new TclEngine();
        // TODO Need better installation story
        var stencilExtension = new StencilExtension(engine, stencil);

        try {
            // TODO: we're going to want save the script text and just evaluate that.
            engine.evalFile(drawingFile);
        } catch (TclException ex) {
            System.out.println("Error in file: " + ex);
            System.exit(1);
        }
    }


    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
