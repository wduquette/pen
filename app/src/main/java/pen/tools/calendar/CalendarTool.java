package pen.tools.calendar;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pen.*;
import pen.calendars.formatter.DateFormat;
import pen.diagram.calendar.YearSpread;
import pen.fx.FX;
import pen.stencil.Stencil;
import pen.tools.FXTool;
import pen.tools.ToolException;
import pen.tools.ToolInfo;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class CalendarTool extends FXTool {
    public static final ToolInfo INFO = new ToolInfo(
        "calendar",
        "datafile.cal | datafile.hist",
        "Displays fictional calendars and events.",
        """
            A calendar file or a history file, this tool will display the
            defined calendars, populated with any relevant dates.  The user
            may choose which calendar to view, and do date conversions.
            """,
        CalendarTool::main
    );
    public static final DateFormat YEAR_ERA =
        new DateFormat("y E");

    //------------------------------------------------------------------------
    // Instance Variables

    private final VBox root = new VBox();
    private final Pane canvasPane = new Pane();
    private final Canvas canvas = new Canvas();
    private final ToolBar statusBar = new ToolBar();
    private final Label statusLabel = new Label();

    private Stencil stencil;

    // Currently Displayed Data
    private Path dataPath;
    private CalendarFile calFile;
    private HistoryFile histFile;
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
        if (argq.size() != 1) {
            printUsage(App.NAME);
            exit(1);
        }
        assert !argq.isEmpty();

        dataPath = new File(argq.poll()).toPath();

        if (!dataPath.toString().endsWith(".cal") &&
            !dataPath.toString().endsWith(".hist")
        ) {
            throw new ToolException("Unrecognized file type: " + dataPath);
        }

        // NEXT, build the GUI
        FX.vbox(root)
            .child(FX.menuBar()
                .menu(FX.menu().text("File")
                    .item(FX.menuItem()
                        .text("Reload")
                        .accelerator("Shortcut+R")
                        .action(this::onReloadData)
                    )
                    .item(FX.menuItem()
                        .text("Exit")
                        .accelerator("Shortcut+Q")
                        .action(this::exit)
                    )
                )
            )
            .child(FX.pane(canvasPane).vgrow()
                .child(FX.node(canvas))
            )
            .child(FX.toolBar(statusBar)
                .add(FX.label(statusLabel)
                    .text("(TODO)")
                    .font(Font.font("Menlo", 14))
                )
            )
        ;

        // NEXT, create the stencil and initialize the TclEngine
        stencil = new Stencil(canvas.getGraphicsContext2D());

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

        // NEXT, load the data
        onReloadData();
    }

    //-------------------------------------------------------------------------
    // Logic

    private void onReloadData() {
        calFile = null;
        histFile = null;
        currentCalendar = null;
        currentYear = 1; // For now

        try {
            if (dataPath.toString().endsWith(".cal")) {
                calFile = DataFiles.loadCalendar(dataPath);
                currentCalendar = calFile.getNames().getFirst();
            } else if (dataPath.toString().endsWith(".hist")) {
                histFile = DataFiles.loadHistory(dataPath);
                calFile = histFile.calendarFile();
                currentCalendar = histFile.primaryCalendar();
            }

            if (calFile == null) {
                showError("No Calendars",
                    "No calendars were found in the data file:\n\n" + dataPath);
            }
        } catch (DataFileException ex) {
            showError("Data Error",
                "Could not read " + dataPath + "\n\n" + ex.getMessage());
        }

        // NEXT, repaint
        repaint();
    }

    private void showError(String header, String content) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void repaint() {
        stencil.background(Color.WHITE);
        stencil.clear();

        if (calFile == null) {
            stencil.draw(Stencil.text()
                .at(20, 20)
                .text("No calendar data loaded")
            );
            return;
        }

        var calendar = calFile.calendars().get(currentCalendar);
        var date = calendar.date(currentYear, 1, 1);

        stencil.draw(new YearSpread()
            .at(10,10)
            .calendar(calendar)
            .year(currentYear)
            .columns(4)
            .title(calendar.format(YEAR_ERA, date))
        );
    }


    //------------------------------------------------------------------------
    // Main

    public static void main(String[] args) {
        launch(args);
    }
}
