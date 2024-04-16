package pen.tools.calendar;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import pen.CalendarFile;
import pen.DataFileException;
import pen.DataFiles;
import pen.HistoryFile;
import pen.calendars.formatter.DateFormat;
import pen.diagram.calendar.YearSpread;
import pen.fx.FX;
import pen.stencil.Stencil;

import java.nio.file.Path;

public class MainView extends VBox {
    public static final DateFormat YEAR_ERA = new DateFormat("y E");

    //-------------------------------------------------------------------------
    // Instance Variables

    //
    // GUI
    //

    private final ToolBar toolBar = new ToolBar();
    private final Pane canvasPane = new Pane();
    private final Canvas canvas = new Canvas();
    private final ToolBar statusBar = new ToolBar();
    private final Label statusLabel = new Label();

    private final Stencil stencil;

    //
    // Data
    //

    private final CalendarTool app;

    private final ObjectProperty<Path> dataPath = new SimpleObjectProperty<>();

    private CalendarFile calFile;
    private HistoryFile histFile;
    private String currentCalendar;
    private int currentYear = 0;

    //-------------------------------------------------------------------------
    // Constructor

    public MainView(CalendarTool app) {
        this.app = app;

        // FIRST, build the GUI
        FX.vbox(this)
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
                        .action(app::exit)
                    )
                )
            )
            .child(FX.toolBar(toolBar)
                .add(FX.button()
                    .text("Previous Year")
                    .action(this::showPreviousYear))
                .add(FX.button()
                    .text("Next Year")
                    .action(this::showNextYear))
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

        // NEXT, listen for events

        // Make the canvas the same size as its parent.
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        // NEXT, repaint on window size change.
        canvas.widthProperty().addListener((p,o,n) -> repaint());
        canvas.heightProperty().addListener((p,o,n) -> repaint());

        // NEXT, load data on data path change
        dataPath.addListener((p,o,n) -> onReloadData());
    }

    //-------------------------------------------------------------------------
    // Logic

    private void onReloadData() {
        calFile = null;
        histFile = null;
        currentCalendar = null;
        currentYear = 1; // For now

        try {
            System.out.println("dataPath = " + getDataPath());
            if (getDataPath().toString().endsWith(".cal")) {
                calFile = DataFiles.loadCalendar(getDataPath());
                currentCalendar = calFile.getNames().getFirst();
            } else if (getDataPath().endsWith(".hist")) {
                histFile = DataFiles.loadHistory(getDataPath());
                calFile = histFile.calendarFile();
                currentCalendar = histFile.primaryCalendar();
            }

            if (calFile == null) {
                showError("No Calendars",
                    "No calendars were found in the data file:\n\n" +
                        getDataPath());
            }
        } catch (DataFileException ex) {
            showError("Data Error",
                "Could not read " + getDataPath() + "\n\n" + ex.getMessage());
        }

        System.out.println("currentCalendar=" + currentCalendar);

        // NEXT, repaint
        repaint();
    }

    private void showPreviousYear() {
        --currentYear;

        if (currentYear == 0) {
            --currentYear;
        }
        repaint();
    }

    private void showNextYear() {
        ++currentYear;
        if (currentYear == 0) {
            ++currentYear;
        }
        repaint();
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

    //-------------------------------------------------------------------------
    // Configuration

    public Path getDataPath() {
        return dataPath.get();
    }

    public void setDataPath(Path path) {
        dataPath.set(path);
    }

    //-------------------------------------------------------------------------
    // Utilities

    private void showError(String header, String content) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
