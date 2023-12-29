package pen;

import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pen.pen.Pen;
import pen.pen.Stencil;

import java.util.function.Consumer;

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
        var pen = stencil.pen();
        sten.clear();
        var w = root.getWidth() - 200;
        var h = root.getHeight() - 200;
        sten.rect()
            .at(100,100)
            .size(w,h)
            .background(Color.LIGHTYELLOW)
            .foreground(Color.PURPLE)
            .lineWidth(2)
            .draw();

        var dim = Pen.getTextSize(Pen.DEFAULT_FONT, "Hello, world!");
        sten.rect().at(50, 50).size(dim.getWidth(), dim.getHeight()).draw();
        pen.save()
            .setTextBaseline(VPos.TOP)
            .fillText("Hello, world!", 50, 50)
            .restore();
    }

    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
