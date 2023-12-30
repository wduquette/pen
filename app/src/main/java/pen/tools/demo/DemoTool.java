package pen.tools.demo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pen.ToolInfo;
import pen.stencil.Pen;
import pen.stencil.Stencil;
import pen.stencil.StencilBuffer;
import pen.stencil.StencilDrawing;
import pen.tools.draw.DrawTool;

import java.io.File;
import java.io.IOException;

import static pen.stencil.Stencil.label;
import static pen.stencil.Stencil.rect;

public class DemoTool extends Application {
    public static final ToolInfo INFO = new ToolInfo(
        "demo",
        "",
        "Displays sample Pen drawings.",
        """
Displays sample drawings; it's also a demo of Pen's internal
Java API.
            """,
        DrawTool::main
    );
    //------------------------------------------------------------------------
    // Instance Variables

    private final StackPane root = new StackPane();
    private final Canvas canvas = new Canvas();
    private Stencil stencil;
    private final StencilDrawing currentDrawing = this::testDrawing;


    //------------------------------------------------------------------------
    // Main-line code

    @Override
    public void start(Stage stage) {
        root.getChildren().add(canvas);
        stencil = new Stencil(canvas.getGraphicsContext2D());
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        Scene scene = new Scene(root, 400, 400);

        stage.setTitle("Pen Demo");
        stage.setScene(scene);
        stage.show();

        canvas.widthProperty().addListener((p,o,n) -> repaint());
        canvas.heightProperty().addListener((p,o,n) -> repaint());
        repaint();

        var buff = new StencilBuffer();
        buff.setMargin(50);
        buff.draw(this::testDrawing);
        try {
            buff.save(new File("test.png"));
        } catch (IOException ex) {
            System.out.println("Failed to save image: " + ex);
        }
    }

    private void repaint() {
        stencil.draw(currentDrawing);
    }

    private void testDrawing(Stencil sten) {
        var w = root.getWidth() - 200;
        var h = root.getHeight() - 200;
        var dim = Pen.getTextSize(Pen.DEFAULT_FONT, "Hello, world!");

        sten.clear()
            .draw(rect().at(100,100).size(w,h)
                .background(Color.LIGHTYELLOW)
                .foreground(Color.PURPLE)
                .lineWidth(2))
            .draw(rect().at(50, 50)
                .size(dim.getWidth(), dim.getHeight()))
            .draw(label().at(50, 50)
                .pos(Pos.TOP_LEFT)
                .text("Hello, world!"))
            ;
    }

    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
