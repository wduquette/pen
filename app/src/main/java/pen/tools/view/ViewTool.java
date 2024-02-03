package pen.tools.view;

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
import pen.apis.StencilExtension;
import pen.fx.FX;
import pen.tcl.TclEngine;
import pen.tools.FXTool;
import pen.tools.ToolInfo;
import pen.stencil.Stencil;
import tcl.lang.TclException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class ViewTool extends FXTool {
    public static final ToolInfo INFO = new ToolInfo(
        "view",
        "[-r] drawing.pen... [folder...]",
        "Displays pen drawings in a window.",
        """
            Given one or more ".pen" files on the command line, this tool
            displays a list of the files and draws the selected file in
            the window.
            
            OPTIONS
            
            -r      If given, the tool will recurse into folders passed on the
                    command line looking for ".pen" files.
            """,
        ViewTool::main
    );

    //------------------------------------------------------------------------
    // Instance Variables

    private final VBox root = new VBox();
    private final SplitPane splitPane = new SplitPane();
    private final ListView<Path> listBox = new ListView<>();
    private final Pane canvasPane = new Pane();
    private final Canvas canvas = new Canvas();
    private final ToolBar statusBar = new ToolBar();
    private final Label statusLabel = new Label();

    private final ObservableList<Path> drawings =
        FXCollections.observableArrayList();

    private TclEngine tcl = new TclEngine();
    private Stencil stencil;
    private String script;

    //------------------------------------------------------------------------
    // Constructor

    public ViewTool() {
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

        var drawingSet = findDrawings(argq);

        if (drawingSet.isEmpty()) {
            throw error("No paths found");
        }

        drawings.addAll(drawingSet);

        // FIRST, build the GUI
        FX.vbox(root)
            .child(FX.menuBar()
                .menu(FX.menu().text("File")
                    .item(FX.menuItem()
                        .text("Reload")
                        .accelerator("Shortcut+R")
                        .action(this::onReloadCurrentDrawing)
                    )
                    .item(FX.menuItem()
                        .text("Exit")
                        .accelerator("Shortcut+Q")
                        .action(this::exit)
                    )
                )
            )
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

        // NEXT, create the stencil and initialize the TclEngine
        stencil = new Stencil(canvas.getGraphicsContext2D());
        tcl = new TclEngine();
        tcl.install(new StencilExtension(stencil));


        // NEXT, pop up the window
        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("pen view");
        stage.setScene(scene);
        stage.show();

        // NEXT, listen for events

        // Select the drawing on selection change
        listBox.getSelectionModel().select(0);
        FX.listenTo(listBox.getSelectionModel().selectedItemProperty(),
            this::onReloadCurrentDrawing);

        // Make the canvas the same size as its parent.
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        // NEXT, repaint on window size change.
        canvas.widthProperty().addListener((p,o,n) -> repaint());
        canvas.heightProperty().addListener((p,o,n) -> repaint());

        onReloadCurrentDrawing();
    }

    //-------------------------------------------------------------------------
    // Logic

    private void onReloadCurrentDrawing() {
        var drawing = listBox.getSelectionModel().getSelectedItem();
        if (drawing != null) {
            script = readFile(drawing);

            if (script == null) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Could not read script");
                alert.setContentText(tcl.interp().getResult().toString());
                alert.showAndWait();
                drawings.remove(drawing);
            }
        }
        repaint();
    }

    private void repaint() {
        stencil.background(Color.WHITE);
        stencil.clear();
        tcl.resetExtensions();

        try {
            if (script != null) {
                tcl.eval(script);
            }
        } catch (TclException ex) {
            // TODO: need better way to show errors alongside partial results.
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error in script");
            alert.setContentText(tcl.interp().getResult().toString());
            alert.showAndWait();
            script = null;
        }
    }

    // Shows the mouse position in the status label.
    private void showMousePosition(MouseEvent evt) {
        statusLabel.setText(String.format("(x=%4.0f, y=%4.0f)",
            evt.getX(), evt.getY()));
    }


    //-------------------------------------------------------------------------
    // File I/O

    private Set<Path> findDrawings(Deque<String> argq) {
        var recurse = false;
        var pathSet = new TreeSet<Path>();

        while (!argq.isEmpty()) {
            var next = argq.poll();

            // Recurse flag
            if (next.equals("-r")) {
                recurse = true;
                continue;
            }

            // Path
            var path = new File(next).toPath();

            if (Files.isRegularFile(path) && path.toString().endsWith(".pen")) {
                pathSet.add(path);
            } else if (recurse && Files.isDirectory(path)) {
                try (var stream = Files.find(path, 10, this::isPenFile)) {
                    stream.forEach(pathSet::add);
                } catch (IOException ex) {
                    throw error("Error finding .pen files", ex);
                }
            } else {
                throw error("Not a .pen file: " + path);
            }
        }

        return pathSet;
    }

    // Is this a regular file with a .pen file type?
    private boolean isPenFile(Path path, BasicFileAttributes attrs) {
        return path.toString().endsWith(".pen") && attrs.isRegularFile();
    }

    private String readFile(Path file) {
        try {
            return Files.readString(file);
        } catch (IOException ex) {
            return null;
        }
    }



    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
