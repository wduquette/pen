package pen.tools.calendar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import pen.apis.StencilExtension;
import pen.fx.FX;
import pen.stencil.Stencil;
import pen.tcl.TclEngine;
import pen.tcl.TclEngineException;
import pen.tools.FXTool;
import pen.tools.ToolInfo;
import tcl.lang.TclException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
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

    //------------------------------------------------------------------------
    // Instance Variables

    private final VBox root = new VBox();
    private final Pane canvasPane = new Pane();
    private final Canvas canvas = new Canvas();
    private final ToolBar statusBar = new ToolBar();
    private final Label statusLabel = new Label();

    private TclEngine tcl = new TclEngine();
    private Stencil stencil;
    private CalendarExtension cal = new CalendarExtension();
    private List<Path> definitionScripts = new ArrayList<>();

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
            .child(FX.pane(canvasPane)
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
        tcl.install(cal);

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
//        canvas.widthProperty().addListener((p,o,n) -> repaint());
//        canvas.heightProperty().addListener((p,o,n) -> repaint());

        onReloadCalendars();
        exit(); // TEMP
    }

    //-------------------------------------------------------------------------
    // Logic

    private void onReloadCalendars() {
        // FIRST, clear previous data.
        tcl.resetExtensions();

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
    }

    private void dumpEras() {
        println("Eras:");

        if (cal.getEras().isEmpty()) {
            println("   None");
        } else {
            for (var e : cal.getEras().entrySet()) {
                println("   " + e.getKey() +
                    ": " + e.getValue().shortForm() +
                    " (" + e.getValue().fullForm() + ")"
                );
            }
        }
    }

    private void dumpWeeks() {
        println("Weeks:");

        if (cal.getWeeks().isEmpty()) {
            println("   None");
            return;
        }

        for (var e : cal.getWeeks().entrySet()) {
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

        if (cal.getMonths().isEmpty()) {
            println("   None");
            return;
        }

        for (var e : cal.getMonths().entrySet()) {
            var m = e.getValue().month();
            println("   " + e.getKey() + ": " + m.fullForm() +
                " (" + m.shortForm() + ", " + m.unambiguousForm() +
                ", " + m.tinyForm() + "), days in 2004: " +
                e.getValue().length().apply(2004)
            );
        }
    }

//    private void repaint() {
//        stencil.background(Color.WHITE);
//        stencil.clear();
//        tcl.resetExtensions();
//
//        try {
//            if (script != null) {
//                tcl.eval(script);
//            }
//        } catch (TclException ex) {
//            // TODO: need better way to show errors alongside partial results.
//            var alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText("Error in script");
//            alert.setContentText(tcl.interp().getResult().toString());
//            alert.showAndWait();
//            script = null;
//        }
//    }

    // Shows the mouse position in the status label.
    private void showMousePosition(MouseEvent evt) {
        statusLabel.setText(String.format("(x=%4.0f, y=%4.0f)",
            evt.getX(), evt.getY()));
    }

    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
