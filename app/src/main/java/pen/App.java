package pen;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pen.pen.Pen;
import pen.pen.Stencil;

import java.util.function.Consumer;

import static pen.pen.Stencil.label;
import static pen.pen.Stencil.rect;

public class App extends Application {
    //------------------------------------------------------------------------
    // Instance Variables

    private final StackPane root = new StackPane();
    private final Canvas canvas = new Canvas();
    private Stencil stencil;
    private final Consumer<Stencil> drawingFunc = this::drawTestDrawing;


    //------------------------------------------------------------------------
    // Main-line code

    @Override
    public void start(Stage stage) {
        root.getChildren().add(canvas);
        stencil = new Stencil(canvas.getGraphicsContext2D());
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        Scene scene = new Scene(root, 400, 400);

        stage.setTitle("Pen");
        stage.setScene(scene);
        stage.show();

        canvas.widthProperty().addListener((p,o,n) -> repaint());
        canvas.heightProperty().addListener((p,o,n) -> repaint());
        repaint();
    }

    private void repaint() {
        drawingFunc.accept(stencil);
    }

    private void drawTestDrawing(Stencil sten) {
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
