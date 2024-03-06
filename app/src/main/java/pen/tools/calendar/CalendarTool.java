package pen.tools.calendar;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pen.App;
import pen.apis.CalendarExtension;
import pen.calendars.formatter.DateFormatter;
import pen.diagram.calendar.YearSpread;
import pen.fx.FX;
import pen.stencil.Stencil;
import pen.tcl.Argq;
import pen.tcl.TclEngine;
import pen.tcl.TclEngineException;
import pen.tools.FXTool;
import pen.tools.ToolInfo;
import tcl.lang.TclException;
import tcl.lang.TclObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CalendarTool extends FXTool {
    public static final ToolInfo INFO = new ToolInfo(
        "calendar",
        "calendar.tcl...",
        "Displays fictional calendars in a window.",
        """
            Given one or more "calendar.tcl" calendar definition scripts on the
            command line, this tool will display yearly and monthly calendars in
            a window, do date conversions, and so forth.
            
            The loaded calendars will be presumed to share the same epoch day.
            """,
        CalendarTool::main
    );
    public static final DateFormatter YEAR_ERA =
        new DateFormatter("y E");

    //------------------------------------------------------------------------
    // Instance Variables

    private final VBox root = new VBox();
    private final Pane canvasPane = new Pane();
    private final Canvas canvas = new Canvas();
    private final ToolBar statusBar = new ToolBar();
    private final Label statusLabel = new Label();

    private TclEngine tcl = new TclEngine();
    private Stencil stencil;
    private final CalendarExtension calendarExtension = new CalendarExtension();
    private final List<Path> definitionScripts = new ArrayList<>();

    // Currently Displayed Data
    private String currentCalendar = null;
    private Integer currentYear = null;

    //------------------------------------------------------------------------
    // Constructor

    public CalendarTool() {
        super(INFO);
    }

    //------------------------------------------------------------------------
    // Main-line code

    @Override
    public void run(Stage stage, Deque<String> argq) {
        // FIRST, parse the command line arguments.
        if (argq.isEmpty()) {
            printUsage(App.NAME);
            exit(1);
        }

        while (!argq.isEmpty()) {
            definitionScripts.add(new File(argq.poll()).toPath());
        }

        // NEXT, build the GUI
        FX.vbox(root)
            .child(FX.menuBar()
                .menu(FX.menu().text("File")
                    .item(FX.menuItem()
                        .text("Reload")
                        .accelerator("Shortcut+R")
                        .action(this::onReloadCalendars)
                    )
                    .item(FX.menuItem()
                        .text("Exit")
                        .accelerator("Shortcut+Q")
                        .action(this::exit)
                    )
                )
            )
            .child(FX.pane(canvasPane).vgrow()
                .child(FX.node(canvas)
                    .onMouseMoved(this::showMousePosition))
            )
            .child(FX.toolBar(statusBar)
                .add(FX.label(statusLabel)
                    .text("(x=    , y=    )")
                    .font(Font.font("Menlo", 14))
                )
            )
        ;

        // NEXT, create the stencil and initialize the TclEngine
        stencil = new Stencil(canvas.getGraphicsContext2D());
        tcl = new TclEngine();
        tcl.install(calendarExtension);
        tcl.add("show", this::cmd_show);

        // NEXT, pop up the window
        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("pen calendar");
        stage.setScene(scene);
        stage.show();

        // NEXT, listen for events

        // Make the canvas the same size as its parent.
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        // NEXT, repaint on window size change.
        canvas.widthProperty().addListener((p,o,n) -> repaint());
        canvas.heightProperty().addListener((p,o,n) -> repaint());

        onReloadCalendars();
    }

    //-------------------------------------------------------------------------
    // Logic

    private void onReloadCalendars() {
        // FIRST, clear previous data.
        tcl.resetExtensions();

        // NEXT, run the scripts
        for (var path : definitionScripts) {
            try {
                var script = Files.readString(path);
                tcl.eval(script);
            } catch (IOException ex) {
                println("Failed to read " + path + ",\n" + ex.getMessage());
            } catch (TclEngineException ex) {
                println("Error on line " + ex.getErrorLine() +
                    " of " + path + ",\n" + ex.getErrorInfo());
            }
        }

        dumpEras();
        dumpWeeks();
        dumpMonths();
        dumpCalendars();

        // NEXT, pick current calendar, etc., if not set.
        if (currentCalendar == null) {
            var list = new ArrayList<>(calendarExtension.getCalendars().keySet());
            currentCalendar = list.getFirst();
        }

        if (currentYear == null) {
            currentYear = 1;
        }

        // NEXT, repaint
        repaint();
    }

    private void dumpEras() {
        println("Eras:");

        if (calendarExtension.getEras().isEmpty()) {
            println("   None");
        } else {
            for (var e : calendarExtension.getEras().entrySet()) {
                println("   " + e.getKey() +
                    ": " + e.getValue().shortForm() +
                    " (" + e.getValue().fullForm() + ")"
                );
            }
        }
    }

    private void dumpWeeks() {
        println("Weeks:");

        if (calendarExtension.getWeeks().isEmpty()) {
            println("   None");
            return;
        }

        for (var e : calendarExtension.getWeeks().entrySet()) {
            println("   " + e.getKey() +
                ": -offset " + e.getValue().epochOffset());
            for (var d : e.getValue().weekdays()) {
                println("      " + d.fullForm() +
                    " (" + d.shortForm() + ", " + d.unambiguousForm() +
                    ", " + d.tinyForm() + ")"
                );
            }
        }
    }

    private void dumpMonths() {
        println("Months:");

        if (calendarExtension.getMonths().isEmpty()) {
            println("   None");
            return;
        }

        for (var e : calendarExtension.getMonths().entrySet()) {
            var m = e.getValue().month();
            println("   " + e.getKey() + ": " + m.fullForm() +
                " (" + m.shortForm() + ", " + m.unambiguousForm() +
                ", " + m.tinyForm() + "), days in 2004: " +
                e.getValue().length().apply(2004)
            );
        }
    }

    private void dumpCalendars() {
        println("Calendars:");

        for (var e : calendarExtension.getCalendars().entrySet()) {
            var c = e.getValue();
            println("   " + e.getKey() +
                ": " + c.era().shortForm() + ", " + c.priorEra().shortForm()
            );
        }
    }

    private void repaint() {
        stencil.background(Color.WHITE);
        stencil.clear();

        if (currentCalendar == null) {
            return;
        }

        var calendar = calendarExtension.getCalendars().get(currentCalendar);
        var date = calendar.date(currentYear, 1, 1);

        stencil.draw(new YearSpread()
            .at(10,10)
            .calendar(calendar)
            .year(currentYear)
            .columns(4)
            .title(YEAR_ERA.format(calendar, date))
        );
    }

    // Shows the mouse position in the status label.
    private void showMousePosition(MouseEvent evt) {
        statusLabel.setText(String.format("(x=%4.0f, y=%4.0f)",
            evt.getX(), evt.getY()));
    }

    //------------------------------------------------------------------------
    // Tool-specific Tcl Commands

    // show calendar ?-year value? ?-month value?
    private void cmd_show(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 1, "?options...?");

        // FIRST, get the calendar.
        var name = argq.next();
        tcl.toMapEntry("calendar", calendarExtension.getCalendars(),
            name);
        currentCalendar = name.toString();

        // If we were provided the options and values as a list, convert it to
        // an Argq.  Note: we lose the command prefix.
        argq = argq.argsLeft() != 1 ? argq : tcl.toArgq(argq.next());

        while (argq.hasNext()) {
            var opt = argq.next().toString();

            switch (opt) {
                case "-year" -> currentYear = toYear(opt, argq);
                default -> throw tcl.unknownOption(opt);
            }
        }
    }

    private int toYear(TclObject arg)
        throws TclException
    {
        var year = tcl.toInteger(arg);
        if (year == 0) {
            throw tcl.expected("calendar year", 0);
        }
        return year;
    }

    private int toYear(String opt, Argq argq)
        throws TclException
    {
        return toYear(tcl.toOptArg(opt, argq));
    }

    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
