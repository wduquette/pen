package pen;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pen.pen.Stencil;

public class App extends Application {
    //------------------------------------------------------------------------
    // Instance Variables

    private final StackPane root = new StackPane();
    private final Canvas canvas = new Canvas();
    private Stencil sten;


    //------------------------------------------------------------------------
    // Main-line code

    @Override
    public void start(Stage stage) {
        root.getChildren().add(canvas);
        sten = new Stencil(canvas.getGraphicsContext2D());
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
        sten.clear();
        var w = root.getWidth() - 200;
        var h = root.getHeight() - 200;
        sten.rect().at(100,100).size(w,h).draw();
    }

    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
