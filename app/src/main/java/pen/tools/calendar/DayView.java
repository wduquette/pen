package pen.tools.calendar;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.StackPane;
import pen.HistoryFile;
import pen.calendars.Date;
import pen.calendars.formatter.DateFormat;
import pen.fx.FX;
import pen.history.Incident;
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
        if (main.getHistoryFile() != null) {
            renderHistory(buff, main.getHistoryFile());
        }

        // NEXT, show it all
        htmlView.show(buff.toString());
    }

    private void renderHistory(HtmlBuilder buff, HistoryFile histFile) {

        var cal = main.selectedCalendar();
        var today = cal.day2date(day.get());

        var incidents = histFile.history().getIncidents().stream()
            .filter(i -> isRelevant(today, i))
            .toList();

        if (incidents.isEmpty()) {
            return;
        }

        buff.h2("Incidents").ul();

        for (var incident : incidents) {
            var date = cal.day2date(incident.moment());
            int anniversary = today.year() - date.year();

            var text = anniversary == 0
                ? incident.label()
                : incident.label() + " (" + number(anniversary) + " anniv.)";

            buff.li(text);
        }

        buff.ulEnd();
    }

    // Do they have the same month/day-of-month?
    private boolean isRelevant(Date today, Incident incident) {
        var date = main.selectedCalendar().day2date(incident.moment());

        if (date.monthOfYear() != today.monthOfYear() ||
            date.dayOfMonth() != today.dayOfMonth()) {
            return false;
        }

        if (incident.isRecurring()) {
            return date.year() <= today.year();
        } else {
            return date.year() == today.year();
        }
    }

    private String number(int num) {
        return switch (num) {
            case 1 -> "1st";
            case 2 -> "2nd";
            case 3 -> "3rd";
            default -> num + "th";
        };
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
