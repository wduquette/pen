package pen.tools.calendar;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pen.calendars.Calendar;
import pen.calendars.formatter.DateFormat;
import pen.fx.FX;

import java.util.ArrayList;
import java.util.List;

import static pen.fx.FX.stackPane;

public class YearView extends StackPane {
    public static final DateFormat YEAR_ERA = new DateFormat("y E");
    public static final String DEFAULT_PLACEHOLDER =
        "No calendar data to display";

    //-------------------------------------------------------------------------
    // Instance Variables

    private final Label yearLabel = new Label();
    private final GridPane monthGrid = new GridPane();
    private final List<MonthView> monthViews = new ArrayList<>();
    private final StackPane placeholderPane = new StackPane();

    private final ObjectProperty<Calendar> calendarProperty =
        new SimpleObjectProperty<>();
    private final IntegerProperty yearProperty =
        new SimpleIntegerProperty(1);
    private final IntegerProperty monthsPerRowProperty =
        new SimpleIntegerProperty(4);
    private final SimpleObjectProperty<Node> placeholderProperty =
        new SimpleObjectProperty<>(new Label(DEFAULT_PLACEHOLDER));

    //-------------------------------------------------------------------------
    // Constructor

    public YearView() {
        stackPane(this)
            .child(FX.vbox().spacing(5)
                .child(FX.label(yearLabel)
                    .alignment(Pos.CENTER)
                )
                .child(FX.gridPane(monthGrid)
                    .hgap(10)
                    .vgap(10)
                    .padding(10)
                )
            )
            .child(FX.stackPane(placeholderPane)
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
        computeYearLabel();
        computeMonths();
    }

    private void computeYearLabel() {
        var day = getCalendar().yearDay(getYear(), 1);
        yearLabel.setText(getCalendar().format(YEAR_ERA, day));
    }

    private void computeMonths() {
        monthViews.clear();
        monthGrid.getChildren().clear();

        var n = getCalendar().monthsInYear();
        var monthsPerRow = monthsPerRowProperty.get();

        for (int i = 0; i < n; i++) {
            var c = i % monthsPerRow;
            var r = i / monthsPerRow;

            var month = new MonthView(i + 1);
            monthViews.add(month);
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
                .child(FX.label(monthLabel))
                .child(FX.gridPane(dateGrid)
                    .hgap(5)
                    .vgap(5)
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
                        .text(weekday)
                        .gridHalignment(HPos.RIGHT)
                    );
            }

            var r = 1;
            for (var dayOfMonth = 1; dayOfMonth <= daysInMonth; dayOfMonth++) {
                var date = cal.date(getYear(), monthOfYear, dayOfMonth);
                var c = date.dayOfWeek() - 1;
                FX.gridPane(dateGrid)
                    .at(c, r, FX.label()
                        .gridHalignment(HPos.RIGHT)
                        .text(String.valueOf(dayOfMonth))
                    );

                // Prepare for next week
                if (date.dayOfWeek() == daysInWeek) {
                    ++r;
                }
            }
        }
    }
}
