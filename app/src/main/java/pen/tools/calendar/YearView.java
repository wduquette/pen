package pen.tools.calendar;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pen.calendars.Calendar;
import pen.calendars.Date;
import pen.calendars.formatter.DateFormat;
import pen.fx.FX;

import java.util.*;

import static pen.fx.FX.stackPane;

@SuppressWarnings("unused")
public class YearView extends StackPane {
    public static final DateFormat YEAR_ERA = new DateFormat("y E");
    public static final String DEFAULT_PLACEHOLDER =
        "No calendar data to display";
    public static final String STYLE_CLASS_SELECTED_DATE =
        "selected-date";

    //-------------------------------------------------------------------------
    // Instance Variables

    private final Label yearLabel = new Label();
    private final GridPane monthGrid = new GridPane();
    private final StackPane placeholderPane = new StackPane();
    private final Map<Date,Button> dateButtons = new HashMap<>();
    private Date selectedDate;

    private final ObjectProperty<Calendar> calendarProperty =
        new SimpleObjectProperty<>();
    private final IntegerProperty yearProperty =
        new SimpleIntegerProperty(1);
    private final IntegerProperty monthsPerRowProperty =
        new SimpleIntegerProperty(4);
    private final SimpleObjectProperty<Node> placeholderProperty =
        new SimpleObjectProperty<>(new Label(DEFAULT_PLACEHOLDER));
    private Runnable onSelectDate;

    //-------------------------------------------------------------------------
    // Constructor

    public YearView() {
        stackPane(this).vgrow()
            .stylesheet(getClass(), "YearView.css")
            .styleClass("yearView")
            .padding(15)
            .child(FX.vbox()
                .alignment(Pos.TOP_CENTER)
                .spacing(5)
                .child(FX.label(yearLabel)
                    .styleClass("year-title")
                )
                .child(FX.gridPane(monthGrid)
                    .alignment(Pos.TOP_CENTER)
                    .hgap(10)
                    .vgap(10)
                    .padding(10)
                )
            )
            .child(FX.stackPane(placeholderPane)
                .vgrow()
                .alignment(Pos.CENTER)
                .visible(false)
                .bareChild(placeholderProperty.get())
            )
            ;

        // Refresh view on parameter change
        calendarProperty.addListener((p,o,n) -> refresh());
        yearProperty.addListener((p,o,n) -> refresh());
        monthsPerRowProperty.addListener((p, o, n) -> refresh());
        placeholderProperty.addListener((p,o,n) -> updatePlaceholder());
    }

    //-------------------------------------------------------------------------
    // Logic

    public void refresh() {
        Integer selectedDay = selectedDate != null
            ? selectedDate.day()
            : null;
        selectedDate = null;

        computeYearLabel();
        computeMonths();

        // Preserve the selected date, if possible.
        if (selectedDay != null) {
            var theDate = getCalendar().day2date(selectedDay);

            if (theDate.year() == getYear()) {
                selectDate(theDate);
            }
        }
    }

    private void computeYearLabel() {
        var day = getCalendar().yearDay(getYear(), 1);
        yearLabel.setText(getCalendar().format(YEAR_ERA, day));
    }

    private void computeMonths() {
        dateButtons.clear();
        monthGrid.getChildren().clear();

        var n = getCalendar().monthsInYear();
        var monthsPerRow = monthsPerRowProperty.get();

        for (int i = 0; i < n; i++) {
            var c = i % monthsPerRow;
            var r = i / monthsPerRow;

            var month = new MonthView(i + 1);
            FX.gridPane(monthGrid)
                .at(c, r, FX.region(month))
            ;
            month.refresh();
        }
    }

    private void updatePlaceholder() {
        placeholderPane.getChildren().clear();
        var placeholder = placeholderProperty.get();
        placeholderPane.getChildren()
            .add(placeholder != null
                ? placeholder : new Label(DEFAULT_PLACEHOLDER));
    }

    private void onDatePress(Date date) {
        selectDate(date);
    }

    public void selectDate(Date date) {
        // FIRST, ignore this if it's the same date.
        if (Objects.equals(date, selectedDate)) {
            return;
        }

        // FIRST, get the buttons
        var oldBtn = dateButtons.get(selectedDate);
        var newBtn = dateButtons.get(date);

        if (oldBtn != null) {
            oldBtn.getStyleClass().remove(STYLE_CLASS_SELECTED_DATE);
        }

        selectedDate = null;

        if (newBtn != null) {
            newBtn.getStyleClass().add(STYLE_CLASS_SELECTED_DATE);
            selectedDate = date;
        }

        if (selectedDate != null) {
            System.out.println("Selected: " + getCalendar().format(selectedDate));
        } else {
            System.out.println("Selection cleared.");
        }

        if (onSelectDate != null) {
            onSelectDate.run();
        }
    }

    //-------------------------------------------------------------------------
    // Accessors

    public Calendar getCalendar() {
        return calendarProperty.get();
    }

    public ObjectProperty<Calendar> calendarProperty() {
        return calendarProperty;
    }

    public void setCalendar(Calendar calendar) {
        this.calendarProperty.set(calendar);
    }

    public int getYear() {
        return yearProperty.get();
    }

    public IntegerProperty yearProperty() {
        return yearProperty;
    }

    public void setYear(int year) {
        this.yearProperty.set(year);
    }

    public Node getPlaceholder() {
        return placeholderProperty.get();
    }

    public SimpleObjectProperty<Node> placeholderProperty() {
        return placeholderProperty;
    }

    public void setPlaceholder(Node placeholder) {
        this.placeholderProperty.set(placeholder);
    }

    public Runnable getOnSelectDate() {
        return onSelectDate;
    }

    public void setOnSelectDate(Runnable handler) {
        this.onSelectDate = handler;
    }

    //-------------------------------------------------------------------------
    // Helper Classes

    // A single month
    private class MonthView extends VBox {
        //---------------------------------------------------------------------
        // Instance Variables

        private final int monthOfYear;

        private final Label monthLabel = new Label();
        private final GridPane dateGrid = new GridPane();


        //---------------------------------------------------------------------
        // Constructor

        public MonthView(int monthOfYear) {
            this.monthOfYear = monthOfYear;

            FX.vbox(this).spacing(5)
                .alignment(Pos.TOP_CENTER)
                .child(FX.label(monthLabel)
                    .styleClass("month-title")
                )
                .child(FX.gridPane(dateGrid)
                    .hgap(1)
                    .vgap(1)
                )
                ;
        }

        //---------------------------------------------------------------------
        // Logic

        public void refresh() {
            dateGrid.getChildren().clear();
            var cal = getCalendar();
            var week = cal.week();
            var daysInWeek = cal.daysInWeek();
            var month = cal.month(monthOfYear);
            var daysInMonth = cal.daysInMonth(getYear(), monthOfYear);

            monthLabel.setText(month.fullForm());

            for (var i = 0; i < daysInWeek; i++) {
                var weekday = week.weekdays().get(i).tinyForm();
                FX.gridPane(dateGrid)
                    .at(i, 0, FX.label()
                        .styleClass("weekday-title")
                        .gridHalignment(HPos.RIGHT)
                        .padding(2)
                        .text(weekday)
                    );
            }

            var r = 1;
            for (var dayOfMonth = 1; dayOfMonth <= daysInMonth; dayOfMonth++) {
                var date = cal.date(getYear(), monthOfYear, dayOfMonth);
                var c = date.dayOfWeek() - 1;
                var btn = new Button();
                dateButtons.put(date, btn);
                FX.gridPane(dateGrid)
                    .at(c, r, FX.button(btn)
                        .clearStyleClasses()
                        .gridHalignment(HPos.RIGHT)
                        .padding(2)
                        .text(String.valueOf(dayOfMonth))
                        .userData(date)
                        .action(() -> onDatePress(date))
                    );

                // Prepare for next week
                if (date.dayOfWeek() == daysInWeek) {
                    ++r;
                }
            }
        }
    }
}
