package pen.tools.annals;

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
import pen.history.History;
import pen.history.HistoryQuery;

import java.nio.file.Path;

@SuppressWarnings("FieldCanBeLocal")
public class MainView extends VBox {
    //-------------------------------------------------------------------------
    // Instance Variables

    //
    // GUI
    //

    private final ComboBox<String> calendarChooser = new ComboBox<>();
    private final ToolBar statusBar = new ToolBar();
    private final Label statusLabel = new Label();
    private final YearView yearView = new YearView(this);
    private final DayView dayView;

    //
    // Data
    //

    private final ObjectProperty<Path> dataPath = new SimpleObjectProperty<>();

    private CalendarFile calFile;
    private HistoryFile histFile;
    private String selectedCalendar;
    private History view;
    private int currentDay = 0;

    //-------------------------------------------------------------------------
    // Constructor

    public MainView(AnnalsTool app) {
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
        view = null;

        try {
            if (getDataPath().toString().endsWith(".cal")) {
                calFile = DataFiles.loadCalendar(getDataPath());
                selectedCalendar = calFile.getNames().getFirst();
                computeInitialDate();
            } else if (getDataPath().toString().endsWith(".hist")) {
                histFile = DataFiles.loadHistory(getDataPath());
                calFile = histFile.calendarFile();
                selectedCalendar = histFile.primaryCalendar();
                computeInitialDate();
            } else {
                showError("No Calendars", "Unknown file type: " + getDataPath());
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

    public Calendar selectedCalendar() {
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
        if (calFile == null) {
            view = null;
            return;
        }

        if (!selectedCalendar().hasMonths() || !selectedCalendar().hasWeeks()) {
            view = null;
            showError("Nothing to display",
                "The selected calendar lacks weeks or months, and so cannot be displayed."
            );
        }

        var calendar = calFile.calendars().get(selectedCalendar);
        var date = calendar.day2date(currentDay);

        var query = new HistoryQuery();
        view = query.expandAnniversaries(selectedCalendar(), date.year())
            .execute(histFile.history());

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

    public HistoryFile getHistoryFile() {
        return histFile;
    }

    public History getView() {
        return view;
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
