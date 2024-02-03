package pen.tools.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pen.App;
import pen.fx.FX;
import pen.tools.FXTool;
import pen.tools.ToolInfo;
import pen.stencil.Stencil;

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

    private ObservableList<Path> drawings = FXCollections.observableArrayList();

    private Stencil stencil;
    private Path currentFile;
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

//        assert !argq.isEmpty();
//        drawingFile = new File(argq.poll());
//        script = readFile(drawingFile);
//
//        if (script == null) {
//            throw error("Could not read file: " + drawingFile);
//        }
//
//        // NEXT, set up the GUI
//        canvas.widthProperty().bind(root.widthProperty());
//        canvas.heightProperty().bind(root.heightProperty());
//
//        var menuBar = new MenuBar();
//
//        var fileMenu = new Menu("File");
//
//        var reloadItem = new MenuItem("Reload Script");
//        reloadItem.setAccelerator(KeyCombination.valueOf("Shortcut+R"));
//        reloadItem.setOnAction(dummy -> reloadAndRepaint());
//
//        var exitItem = new MenuItem("Exit");
//        exitItem.setAccelerator(KeyCombination.valueOf("Shortcut+Q"));
//        exitItem.setOnAction(dummy -> System.exit(0));
//
//        fileMenu.getItems().addAll(reloadItem, exitItem);
//
//        menuBar.getMenus().add(fileMenu);
//
//        VBox.setVgrow(hull, Priority.ALWAYS);
//        hull.getChildren().add(canvas);
//
//        root.getChildren().addAll(menuBar, hull);
//
//        stencil = new Stencil(canvas.getGraphicsContext2D());
//
        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("pen view");
        stage.setScene(scene);
        stage.show();
//
//        // NEXT, repaint on window size change, and on user request.
//        canvas.widthProperty().addListener((p,o,n) -> repaint());
//        canvas.heightProperty().addListener((p,o,n) -> repaint());
//
//        repaint();
    }

    //-------------------------------------------------------------------------
    // Logic

    private void onReloadCurrentDrawing() {
        println("TODO: onReloadCurrentDrawing()");
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
                try {
                    Files.find(path, 10, this::isPenFile).forEach(pathSet::add);
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

    private String readFile(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException ex) {
            return null;
        }
    }

//    private void reloadAndRepaint() {
//        script = readFile(drawingFile);
//        repaint();
//    }
//
//    private void repaint() {
//        stencil.clear();
//        var engine = new TclEngine();
//        engine.install(new StencilExtension(stencil));
//
//        try {
//            if (script != null) {
//                engine.eval(script);
//            } else {
//                // TODO: Do better
//                System.out.println("No script loaded.");
//            }
//        } catch (TclException ex) {
//            // TODO: need better way to show errors alongside partial results.
//            var alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText("Error in script");
//            alert.setContentText(engine.interp().getResult().toString());
//            alert.showAndWait();
//            script = null;
//        }
//    }


    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
