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
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import pen.calendars.*;
import pen.diagram.calendar.MonthSpread;
import pen.diagram.calendar.YearSpread;
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
                .item(FX.listView(listBox)
                    .splitResizableWithParent(false)
                    .setItems(drawings)
                )
                .item(FX.pane(canvasPane)
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
            drawing("Test Drawing",           this::testDrawing),
            drawing("Month Spread (Stencil)", this::testMonthSpreadStencil),
            drawing("Month Spread",           this::testMonthSpread),
            drawing("Year Spread",            this::testYearSpread),
            drawing("Shapes",                 this::testShapes),
            drawing("Rotation",               this::testRotation),
            drawing("Symbols",                this::testSymbols),
            drawing("Line Symbols",           this::testLineSymbols),
            drawing("Boxed Label",            this::testBoxedLabels)
        );

    private void testDrawing(Stencil sten) {
        sten.clear(Color.WHITE);
        sten.draw(boxedText()
            .at(250,250)
            .minSize(200,200)
            .tack(Tack.SOUTHEAST)
            .text("ABC")
        );
    }

    private void testMonthSpreadStencil(Stencil sten) {
        sten.clear(Color.WHITE);
        var date = ME.date(1011, 1, 1);
        var funDay = ME.date2day(ME.date(1011, 1, 1));
        var daysInMonth = ME.daysInMonth(1011, 1);
        var daysInWeek = ME.daysInWeek();
        var titleFont = new PenFont.Builder("title")
            .family("sans-serif").weight(FontWeight.BOLD).size(14).build();
        var dayFont = new PenFont.Builder("day")
            .family("sans-serif").weight(FontWeight.BOLD).size(12).build();
        var dateFont = PenFont.SANS12;
        var titleHeight = titleFont.getHeight();
        var dayHeight = dayFont.getHeight();
        var dateHeight = dateFont.getHeight();
        var titlePad = 10;
        var pad = 5;

        var title = date.month().fullForm();

        // NEXT, compute the width of a month.
        var dateWidth = Pen.getTextWidth(dateFont, "99");
        var monthWidth = dateWidth + (daysInWeek - 1)*(dateWidth + pad);

        // NEXT, draw the title
        stencil.savePen().translate(10 + monthWidth/2.0, 10);
        stencil.draw(text().at(0, 0).text(title).font(titleFont).tack(Tack.NORTH));
        stencil.restorePen();

        // NEXT, draw the day abbreviations
        stencil.savePen().translate(10 + dateWidth, 10 + titleHeight + titlePad);
        for (var i = 0; i < 7; i++) {
            var x = i*(dateWidth + pad);
            stencil.draw(text()
                .at(x, 0)
                .text(ME.week().weekdays().get(i).tinyForm())
                .tack(Tack.NORTHEAST)
                .font(dayFont)
            );
        }
        stencil.restorePen();

        // NEXT, get the start day
        var startDayOfWeek = ME.day2dayOfWeek(funDay);
        int startDate = 1 - (startDayOfWeek - 1);

        var numWeeks = 1 + (daysInMonth/daysInWeek);

        stencil.savePen()
            .translate(10 + dateWidth, 10 + titleHeight + titlePad + dateHeight + pad);
        for (int w = 0; w < numWeeks; w++) {
            var y = w*(dayHeight + pad);
            for (int i = 0; i < daysInWeek; i++) {
                var x = i*(dateWidth + pad);

                var dayOfMonth = startDate + w*daysInWeek + i;
                if (dayOfMonth < 1 || dayOfMonth > daysInMonth) {
                    continue;
                }
                stencil.draw(text()
                    .at(x, y)
                    .text(Integer.toString(dayOfMonth))
                    .tack(Tack.NORTHEAST)
                    .font(dateFont)
                );
            }
        }
    }

    private void testMonthSpread(Stencil sten) {
        sten.clear(Color.WHITE);
        var spread = new MonthSpread()
            .at(40, 10)
            .calendar(ME)
            .title(ME.month(6).fullForm())
            .year(1011)
            .monthOfYear(6)
            ;
        sten.draw(spread);
    }


    private void testYearSpread(Stencil sten) {
        sten.clear(Color.WHITE);
        var spread = new YearSpread()
            .at(10, 10)
            .calendar(ME)
            .title("1011 " + ME.era())
            .year(1011)
            ;
        sten.draw(spread);
    }

    private void testShapes(Stencil sten) {
        sten.clear(Color.WHITE);

        var w = 80;
        var h = 60;
        var pad = 10;
        var x0 = 10 + w/2;

        var x = x0;
        var y = 10 + h/2;
        sten.draw(rectangle().at(x,y).size(w,h).tack(Tack.CENTER)
            .background(Color.LIGHTYELLOW)
        );
        sten.draw(text().at(x,y).text("rectangle").tack(Tack.CENTER));

        x += w + pad;
        sten.draw(oval().at(x,y).size(w,h).tack(Tack.CENTER)
            .background(Color.LIGHTYELLOW)
        );
        sten.draw(text().at(x,y).text("oval").tack(Tack.CENTER));

        x = x0;
        y += h + pad;
        sten.draw(boxedText().at(x,y).text("boxedText")
            .pad(10)
            .tack(Tack.CENTER)
            .background(Color.LIGHTYELLOW)
        );

        x += w + pad;
        sten.draw(oval().at(x,y).diameter(h).tack(Tack.CENTER)
            .background(Color.LIGHTYELLOW)
        );
        sten.draw(text().at(x,y).text("oval").tack(Tack.CENTER));
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
    // Calendar values
    private static final Week ME_WEEK = new Week(StandardWeek.DAYS, 1);
    private static final BasicCalendar ME = new BasicCalendar.Builder()
        .era(new Era("ME", "Modern Era"))
        .priorEra(new Era("BME", "Before Modern Era"))
        .epochOffset(-978 * 366)
        .month(StandardMonth.JANUARY, 31)
        .month(StandardMonth.FEBRUARY, 28)
        .month(StandardMonth.MARCH, 31)
        .month(StandardMonth.APRIL, 30)
        .month(StandardMonth.MAY, 31)
        .month(StandardMonth.JUNE, 30)
        .month(StandardMonth.JULY, 31)
        .month(StandardMonth.AUGUST, 31)
        .month(StandardMonth.SEPTEMBER, 30)
        .month(StandardMonth.OCTOBER, 31)
        .month(StandardMonth.NOVEMBER, 31)
        .month(StandardMonth.DECEMBER, 31)
        .week(ME_WEEK)
        .build();

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
