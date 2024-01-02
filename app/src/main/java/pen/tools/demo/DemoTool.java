package pen.tools.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pen.tools.ToolInfo;
import pen.stencil.Pen;
import pen.stencil.Stencil;
import pen.stencil.StencilDrawing;
import pen.tools.draw.DrawTool;

import static pen.stencil.Stencil.*;

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

    private final VBox root = new VBox();
    private final SplitPane splitPane = new SplitPane();
    private final ListView<DemoDrawing> listBox = new ListView<>();
    private final StackPane canvasPane = new StackPane();
    private final Canvas canvas = new Canvas();
    private Stencil stencil;
    private DemoDrawing currentDrawing;

    //------------------------------------------------------------------------
    // Main-line code

    // TODO: Geometry management isn't right: canvasPane won't shrink!

    @Override
    public void start(Stage stage) {
        // listBox
        SplitPane.setResizableWithParent(listBox, false);
        listBox.setItems(drawings);

        // CanvasPane
        SplitPane.setResizableWithParent(canvasPane, true);
        canvasPane.getChildren().add(canvas);
        stencil = new Stencil(canvas.getGraphicsContext2D());
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        // splitPane
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        splitPane.getItems().addAll(listBox, canvasPane);
        splitPane.setDividerPosition(0, 0.2);

        // root
        root.getChildren().add(splitPane);

        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("pen demo");
        stage.setScene(scene);
        stage.show();

        canvas.widthProperty().addListener((p,o,n) -> repaint());
        canvas.heightProperty().addListener((p,o,n) -> repaint());

        currentDrawing = drawings.get(0);
        repaint();
    }

    private void repaint() {
        stencil.draw(currentDrawing.drawing());
    }

    //-------------------------------------------------------------------------
    // Drawings

    private DemoDrawing drawing(String name, StencilDrawing drawing) {
        return new DemoDrawing(name, drawing);
    }

    private final ObservableList<DemoDrawing> drawings =
        FXCollections.observableArrayList(
            drawing("Test Drawing", this::testDrawing)
        );

    private void testDrawing(Stencil sten) {
        stencil.draw(rect().at(10,10).size(100,60).lineWidth(2).background(Color.LIGHTYELLOW));
        stencil.draw(line().to(10,10).to(110,70));
        stencil.draw(line().to(10,70).to(110,10));
        stencil.draw(label().at(60,80).pos(Pos.TOP_CENTER).text("Stencil Test"));
    }

    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
