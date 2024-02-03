package pen.tools.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pen.fx.FX;
import pen.stencil.*;
import pen.tools.FXTool;
import pen.tools.ToolInfo;
import pen.tools.draw.DrawTool;

import java.util.Deque;

import static pen.stencil.Stencil.*;

/**
 * The application class for the "pen demo" tool.
 */
public class DemoTool extends FXTool {
    /**
     * Tool information for this tool, for use by the launcher.
     */
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

    /**
     * Creates the application object.
     */
    public DemoTool() {
        super(INFO);
    }

    @Override
    public void run(Stage stage, Deque<String> argq) {
        // FIRST, build the GUI
        FX.vbox(root)
            .child(FX.splitPane(splitPane)
                .vgrow()
                .add(FX.listView(listBox)
                    .splitResizableWithParent(false)
                    .setItems(drawings)
                )
                .add(FX.pane(canvasPane)
                    .splitResizableWithParent(true)
                    .child(FX.node(canvas)
                        .onMouseMoved(this::showMousePosition))
                )
                .setDividerPosition(0, 0.2)
            )
            .child(FX.toolBar(statusBar)
                .add(FX.label(statusLabel)
                    .text("(x=    , y=    )")
                    .font(Font.font("Menlo", 14))
                )
            )
            ;

        // NEXT, configure ancillary objects
        stencil = new Stencil(canvas.getGraphicsContext2D());

        // NEXT, set up the event handling

        // Make the canvas the same size as its parent.
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        // Repaint if the canvas size changes
        FX.listenTo(canvasPane.widthProperty(), this::repaint);
        FX.listenTo(canvasPane.heightProperty(), this::repaint);

        // Draw the selected drawing.
        listBox.getSelectionModel().selectedIndexProperty().addListener((p,o,n) -> {
            if (n != null) {
                currentDrawing = drawings.get(n.intValue());
                repaint();
            }
        });

        // NEXT, configure the stage
        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("pen demo");
        stage.setScene(scene);
        stage.show();

        // NEXT, set and draw the default drawing
        currentDrawing = drawings.getFirst();
        repaint();
    }

    private void repaint() {
        if (currentDrawing != null) {
            stencil.background(Color.WHITE);
            stencil.clear();
            stencil.pen().reset(); // TODO: Use stencil's own method.
            stencil.draw(currentDrawing.drawing());
        }
    }

    private void showMousePosition(MouseEvent evt) {
        statusLabel.setText(String.format("(x=%4.0f, y=%4.0f)",
            evt.getX(), evt.getY()));
    }

    //-------------------------------------------------------------------------
    // Drawings

    private DemoDrawing drawing(String name, Drawing drawing) {
        return new DemoDrawing(name, drawing);
    }

    private final ObservableList<DemoDrawing> drawings =
        FXCollections.observableArrayList(
            drawing("Test Drawing", this::testDrawing),
            drawing("Shapes",       this::testShapes),
            drawing("Rotation",     this::testRotation),
            drawing("Symbols",      this::testSymbols),
            drawing("Line Symbols", this::testLineSymbols),
            drawing("Boxed Label",  this::testBoxedLabels)
        );

    private void testDrawing(Stencil sten) {
        sten.clear(Color.PINK);
    }

    private void testShapes(Stencil sten) {
        sten.clear(Color.WHITE);
        sten.draw(rectangle().at(10,10).size(100,60).background(Color.LIGHTYELLOW));
        sten.draw(line().to(10,10).to(110,70));
        sten.draw(line().to(10,70).to(110,10));
        sten.draw(text().at(60,80).tack(Tack.NORTH).text("Stencil Test"));

        sten.draw(rectangle().at(60,150).size(60,40).tack(Tack.SOUTH));
        sten.draw(rectangle().at(60,150).size(12,8).tack(Tack.SOUTH));
    }

    private void testRotation(Stencil sten) {
        sten.clear(Color.WHITE);

        for (var degrees = 0; degrees < 360; degrees += 30) {
            sten.pen().save().translate(150,150).rotate(degrees);
            sten.draw(line().to(0,0).to(100,0));
            sten.draw(text().at(110, 0).tack(Tack.WEST).text(Double.toString(degrees)));
            sten.pen().restore();
        }
    }

    private void testSymbols(Stencil sten) {
        sten.clear(Color.WHITE);

        drawSymbols(sten,  30, Symbol.ARROW_SOLID);
        drawSymbols(sten,  90, Symbol.ARROW_OPEN);
        drawSymbols(sten, 150, Symbol.DOT_OPEN_OFFSET);
        drawSymbols(sten, 240, Symbol.DOT_SOLID);
    }

    private void drawSymbols(Stencil sten, double x, Symbol symbol) {
        sten.draw(line().to(x,30).to(x, 195).foreground(Color.RED));
        for (int i = 0; i < 12; i++) {
            var y = 30 + i*15;
            var degrees = 30.0*i;
            sten.savePen()
                .translate(x, y)
                .rotate(degrees)
                .draw(symbol().at(0,0).symbol(symbol))
                .restorePen();
        }
    }

    private void testLineSymbols(Stencil sten) {
        sten.clear(Color.WHITE);

        sten.savePen();
        sten.translate(100,100);
        sten.draw(line().to( 15,  0).to( 50,   0).start(Symbol.ARROW_SOLID).end(Symbol.ARROW_OPEN));
        sten.draw(line().to( 15,-15).to( 50, -50).start(Symbol.ARROW_SOLID).end(Symbol.ARROW_OPEN));
        sten.draw(line().to(  0,-15).to(  0, -50).start(Symbol.ARROW_SOLID).end(Symbol.ARROW_OPEN));
        sten.draw(line().to(-15,-15).to(-50, -50).start(Symbol.ARROW_SOLID).end(Symbol.ARROW_OPEN));
        sten.draw(line().to(-15,  0).to(-50,   0).start(Symbol.ARROW_SOLID).end(Symbol.ARROW_OPEN));
        sten.draw(line().to(-15, 15).to(-50,  50).start(Symbol.ARROW_SOLID).end(Symbol.ARROW_OPEN));
        sten.draw(line().to(  0, 15).to(  0,  50).start(Symbol.ARROW_SOLID).end(Symbol.ARROW_OPEN));
        sten.draw(line().to( 15, 15).to( 50,  50).start(Symbol.ARROW_SOLID).end(Symbol.ARROW_OPEN));
        sten.restorePen();
    }

    private void testBoxedLabels(Stencil sten) {
        sten.clear();
        sten.draw(symbol().at(100,100).symbol(Symbol.ARROW_SOLID));
        sten.draw(boxedText()
            .at(100,100)
            .tack(Tack.WEST)
            .text("Hello, World!")
            .pad(20)
            .background(Color.LIGHTYELLOW)
            .textColor(Color.BLUE)
        );
    }

    //------------------------------------------------------------------------
    // Main

    /**
     * The tool's main method.  Launches the Java Application with the
     * command-line arguments.
     * @param args The command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
