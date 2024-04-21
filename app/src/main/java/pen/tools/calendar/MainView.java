package pen.tools.calendar;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import pen.CalendarFile;
import pen.DataFileException;
import pen.DataFiles;
import pen.HistoryFile;
import pen.calendars.Calendar;
import pen.fx.FX;

import java.nio.file.Path;

public class MainView extends VBox {
    //-------------------------------------------------------------------------
    // Instance Variables

    //
    // GUI
    //

    private final ComboBox<String> calendarChooser = new ComboBox<>();
    private final ToolBar statusBar = new ToolBar();
    private final Label statusLabel = new Label();
    private final YearView yearView = new YearView();
    private final DayView dayView;

    //
    // Data
    //

    private final CalendarTool app;

    private final ObjectProperty<Path> dataPath = new SimpleObjectProperty<>();

    private CalendarFile calFile;
    private HistoryFile histFile;
    private String selectedCalendar;
    private int currentDay = 0;

    //-------------------------------------------------------------------------
    // Constructor

    public MainView(CalendarTool app) {
        this.app = app;
        this.dayView = new DayView(this);

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
            .child(FX.toolBar()
                .add(FX.button()
                    .text("<<")
                    .action(this::showPreviousYear))
                .add(FX.comboBox(calendarChooser)
                    .editable(false)
                    .placeholderText("No calendars")
                    .action(this::chooseCalendar)
                )
                .add(FX.button()
                    .text(">>")
                    .action(this::showNextYear))
            )
            .child(FX.splitPane().vgrow()
                .item(FX.region(yearView)
                    .splitResizableWithParent(true)
                )
                .item(FX.region(dayView).hgrow())
                .setDividerPosition(0, 0.7)
            )
            .child(FX.toolBar(statusBar)
                .add(FX.label(statusLabel)
                    .text("(TODO)")
                    .font(Font.font("Menlo", 14))
                )
            )
            ;

        // NEXT, load data on data path change
        dataPath.addListener((p,o,n) -> onReloadData());

        // NEXT, update the display on day selection.
        yearView.setOnSelectDate(this::onSelectDate);
    }

    //-------------------------------------------------------------------------
    // Logic

    private void onReloadData() {
        calFile = null;
        histFile = null;
        selectedCalendar = null;
        currentDay = 0; // For now

        try {
            if (getDataPath().toString().endsWith(".cal")) {
                calFile = DataFiles.loadCalendar(getDataPath());
                selectedCalendar = calFile.getNames().getFirst();
                computeInitialDate();
            } else if (getDataPath().endsWith(".hist")) {
                histFile = DataFiles.loadHistory(getDataPath());
                calFile = histFile.calendarFile();
                selectedCalendar = histFile.primaryCalendar();
                computeInitialDate();
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

        if (calFile != null) {
            calendarChooser.getItems().clear();
            calendarChooser.getItems().addAll(calFile.getNames());
        }
        calendarChooser.setValue(selectedCalendar);

        // NEXT, repaint
        repaint();
    }

    private void computeInitialDate() {
        var cal = selectedCalendar();
        var today = calFile.today();
        var yearDay = cal.day2yearDay(today);
        var newYears = cal.yearDay(yearDay.year(), 1);
        currentDay = cal.yearDay2day(newYears);
    }

    // Updates the selected calendar and repaints.
    private void chooseCalendar() {
        if (calendarChooser.getValue() != null) {
            selectedCalendar = calendarChooser.getValue();
        }

        repaint();
    }

    private Calendar selectedCalendar() {
        return calFile.calendars().get(selectedCalendar);
    }

    private void showPreviousYear() {
        var cal = selectedCalendar();
        var currentYear = cal.day2yearDay(currentDay).year();

        --currentYear;

        if (currentYear == 0) {
            --currentYear;
        }

        currentDay = cal.yearDay2day(cal.yearDay(currentYear, 1));
        repaint();
    }

    private void showNextYear() {
        var cal = selectedCalendar();
        var currentYear = cal.day2yearDay(currentDay).year();

        ++currentYear;

        if (currentYear == 0) {
            ++currentYear;
        }

        currentDay = cal.yearDay2day(cal.yearDay(currentYear, 1));
        repaint();
    }

    private void repaint() {
        var calendar = calFile.calendars().get(selectedCalendar);
        var date = calendar.day2date(currentDay);

        yearView.setCalendar(calendar);
        yearView.setYear(date.year());
        dayView.setDay(currentDay);
    }

    private void onSelectDate() {
        yearView.getSelectedDay().ifPresent(dayView::setDay);
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
    // Queries

    public CalendarFile getCalendarFile() {
        return calFile;
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
