package pen.tools.calendar;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.VBox;
import pen.calendars.formatter.DateFormat;
import pen.fx.FX;

/**
 * A widget that displays whatever is known about a particular day.
 */
public class DayView extends VBox {
    public static DateFormat FULL_DATE = new DateFormat("WWWW',' MMMM d',' y E");

    //-------------------------------------------------------------------------
    // Instance Variables

    // The MainView: the source of historical and calendrical truth
    private final MainView main;

    // The current day
    private final IntegerProperty day = new SimpleIntegerProperty();

    //-------------------------------------------------------------------------
    // Constructor

    public DayView(MainView main) {
        this.main = main;
        FX.vbox(this)
            .styleClass("dayView")
            .stylesheet(getClass(), "DayView.css")
            .padding(15)
            ;
        day.addListener((p,o,n) -> render());
        render();
    }

    //-------------------------------------------------------------------------
    // Logic

    // Renders information about the day.
    private void render() {
        getChildren().clear();

        if (main.getCalendarFile() == null) {
            FX.vbox(this)
                .child(FX.label()
                    .fixed()
                    .text("No calendar data."));
            return;
        }

        // FIRST, render the current dates for each calendar.
        for (var cal : main.getCalendarFile().calendars().values()) {
            var dateString = cal.format(FULL_DATE, day.get());

            FX.vbox(this)
                .child(FX.label()
                    .fixed()
                    .text(dateString))
                ;
        }

        // NEXT, render significant events on this day.
        // (requires History updates)
    }

    //-------------------------------------------------------------------------
    // Configuration


    public int getDay() {
        return day.get();
    }

    public void setDay(int day) {
        this.day.set(day);
    }
}
