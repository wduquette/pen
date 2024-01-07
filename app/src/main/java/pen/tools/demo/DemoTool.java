package pen.tools.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pen.fx.FX;
import pen.stencil.Tack;
import pen.tools.ToolInfo;
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
    private final Pane canvasPane = new Pane();
    private final Canvas canvas = new Canvas();
    private final ToolBar statusBar = new ToolBar();
    private final Label statusLabel = new Label();
    private Stencil stencil;
    private DemoDrawing currentDrawing;

    //------------------------------------------------------------------------
    // Main-line code

    // TODO: Geometry management isn't right: canvasPane won't shrink!

    @Override
    public void start(Stage stage) {
        FX.vbox(root)
            .add(FX.splitPane(splitPane)
                .vgrow()
                .addBare(listBox)
                .add(FX.pane(canvasPane)
                    .addBare(canvas)
                )
                .setDividerPosition(0, 0.2)
            )
            .add(FX.toolBar(statusBar)
                .add(FX.label(statusLabel).text("(x=    , y=    )"))
            )
            ;

        // listBox
        SplitPane.setResizableWithParent(listBox, false);
        listBox.setItems(drawings);

        // CanvasPane
        SplitPane.setResizableWithParent(canvasPane, true);
//        canvasPane.getChildren().add(canvas);
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());
        stencil = new Stencil(canvas.getGraphicsContext2D());
        canvas.setOnMouseMoved(evt ->
            statusLabel.setText(String.format("(x=%4.0f, y=%4.0f)",
                evt.getX(), evt.getY())));

        // splitPane
//        VBox.setVgrow(splitPane, Priority.ALWAYS);
//        splitPane.getItems().addAll(listBox, canvasPane);
//        splitPane.setDividerPosition(0, 0.2);

        // statusBar
//        statusBar.getItems().add(statusLabel);

        // root
//        root.getChildren().addAll(splitPane, statusBar);

        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("pen demo");
        stage.setScene(scene);
        stage.show();

        canvasPane.widthProperty().addListener((p,o,n) -> repaint());
        canvasPane.heightProperty().addListener((p,o,n) -> repaint());

        currentDrawing = drawings.get(0);
        repaint();
    }

    private void repaint() {
        stencil.clear();
        stencil.draw(currentDrawing.drawing());
        System.out.println("Size=" + stencil.getImageSize());
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
//        stencil.pen().translate(100, 0);
//        stencil.pen().scale(2,2);
//        stencil.pen().rotate(45.0);
        stencil.draw(rect().at(10,10).size(100,60).lineWidth(2).background(Color.LIGHTYELLOW));
        stencil.draw(line().to(10,10).to(110,70));
        stencil.draw(line().to(10,70).to(110,10));
        stencil.draw(label().at(60,80).tack(Tack.NORTH).text("Stencil Test"));

        stencil.draw(rect().at(60,150).size(60,40).tack(Tack.SOUTH));
        stencil.draw(rect().at(60,150).size(12,8).tack(Tack.SOUTH));
    }

    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
