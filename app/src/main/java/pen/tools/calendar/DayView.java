package pen.tools.calendar;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pen.calendars.formatter.DateFormat;
import pen.fx.FX;
import pen.util.HtmlBuilder;
import pen.widgets.HtmlView;

/**
 * A widget that displays whatever is known about a particular day.
 */
public class DayView extends StackPane {
    public static DateFormat FULL_DATE =
        new DateFormat("d MMMM y',' E '('WWWW')'");

    //-------------------------------------------------------------------------
    // Instance Variables

    // The MainView: the source of historical and calendrical truth
    private final MainView main;

    // The current day
    private final IntegerProperty day = new SimpleIntegerProperty();

    // The HtmlView
    private final HtmlView htmlView = new HtmlView();

    //-------------------------------------------------------------------------
    // Constructor

    public DayView(MainView main) {
        this.main = main;
        FX.stackPane(this)
            .bareChild(htmlView)
            ;
        day.addListener((p,o,n) -> render());
        render();
    }

    //-------------------------------------------------------------------------
    // Logic

    // Renders information about the day.
    private void render() {
        var buff = new HtmlBuilder();

        if (main.getCalendarFile() == null) {
            buff.p("No calendar date.");
            htmlView.show(buff.toString());
            return;
        }

        // FIRST, render the current dates for each calendar.
        buff.h2("Today");
        buff.ul();
        for (var cal : main.getCalendarFile().calendars().values()) {

            var dateString = cal.format(FULL_DATE, day.get());

            buff.li(dateString);
        }
        buff.ulEnd();

        // NEXT, render significant events on this day.
        // (requires History updates)

        htmlView.show(buff.toString());
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
