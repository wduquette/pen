package pen.tools.view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pen.stencil.Pen;
import pen.stencil.Stencil;
import pen.stencil.StencilBuffer;
import pen.stencil.StencilDrawing;

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

    //------------------------------------------------------------------------
    // Main-line code

    @Override
    public void start(Stage stage) {
        // FIRST, parse the command line arguments.
        var argq = new ArrayDeque<>(getParameters().getRaw());
        System.out.println("args: " + argq);
        System.exit(0);

//
//        // NEXT, set up the GUI
//        root.getChildren().add(canvas);
//        stencil = new Stencil(canvas.getGraphicsContext2D());
//        canvas.widthProperty().bind(root.widthProperty());
//        canvas.heightProperty().bind(root.heightProperty());
//
//        Scene scene = new Scene(root, 400, 400);
//
//        stage.setTitle("Pen View");
//        stage.setScene(scene);
//        stage.show();
//
//        canvas.widthProperty().addListener((p,o,n) -> repaint());
//        canvas.heightProperty().addListener((p,o,n) -> repaint());
//        repaint();
//
//        var buff = new StencilBuffer();
//        buff.setMargin(50);
//        buff.draw(this::testDrawing);
//        try {
//            buff.save(new File("test.png"));
//        } catch (IOException ex) {
//            System.out.println("Failed to save image: " + ex);
//        }
    }

    private void repaint() {
//        stencil.draw(currentDrawing);
    }

    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
