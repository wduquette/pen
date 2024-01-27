package pen.tools.view;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pen.App;
import pen.tools.FXTool;
import pen.tools.ToolInfo;
import pen.apis.StencilExtension;
import pen.stencil.Stencil;
import pen.tcl.TclEngine;
import tcl.lang.TclException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Deque;

public class ViewTool extends FXTool {
    public static final ToolInfo INFO = new ToolInfo(
        "view",
        "drawing.tcl",
        "Displays a pen drawing in a window.",
        """
            Given a Pen drawing script, displays the drawing
            in a window.
            """,
        ViewTool::main
    );

    //------------------------------------------------------------------------
    // Instance Variables

    private final VBox root = new VBox();
    private final StackPane hull = new StackPane();
    private final Canvas canvas = new Canvas();
    private Stencil stencil;
    private File drawingFile;
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
        argq.poll(); // Skip the tool name

        if (argq.size() != 1) {
            App.showUsage(INFO);
            System.exit(1);
        }

        drawingFile = new File(argq.poll());
        script = readFile(drawingFile);

        if (script == null) {
            System.out.println("Could not read file: " + drawingFile);
            System.exit(1);
        }

        // NEXT, set up the GUI
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        var menuBar = new MenuBar();

        var fileMenu = new Menu("File");

        var reloadItem = new MenuItem("Reload Script");
        reloadItem.setAccelerator(KeyCombination.valueOf("Shortcut+R"));
        reloadItem.setOnAction(dummy -> reloadAndRepaint());

        var exitItem = new MenuItem("Exit");
        exitItem.setAccelerator(KeyCombination.valueOf("Shortcut+Q"));
        exitItem.setOnAction(dummy -> System.exit(0));

        fileMenu.getItems().addAll(reloadItem, exitItem);

        menuBar.getMenus().add(fileMenu);

        VBox.setVgrow(hull, Priority.ALWAYS);
        hull.getChildren().add(canvas);

        root.getChildren().addAll(menuBar, hull);

        stencil = new Stencil(canvas.getGraphicsContext2D());

        Scene scene = new Scene(root, 400, 400);

        stage.setTitle("pen view " + drawingFile);
        stage.setScene(scene);
        stage.show();

        // NEXT, repaint on window size change, and on user request.
        canvas.widthProperty().addListener((p,o,n) -> repaint());
        canvas.heightProperty().addListener((p,o,n) -> repaint());

        repaint();
    }

    private String readFile(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException ex) {
            return null;
        }
    }

    private void reloadAndRepaint() {
        script = readFile(drawingFile);
        repaint();
    }

    private void repaint() {
        stencil.clear();
        var engine = new TclEngine();
        // TODO Need better installation story
        var stencilExtension = new StencilExtension(engine, stencil);

        try {
            if (script != null) {
                engine.eval(script);
            } else {
                // TODO: Do better
                System.out.println("No script loaded.");
            }
        } catch (TclException ex) {
            // TODO: need better way to show errors alongside partial results.
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error in script");
            alert.setContentText(engine.interp().getResult().toString());
            alert.showAndWait();
            script = null;
        }
    }


    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
