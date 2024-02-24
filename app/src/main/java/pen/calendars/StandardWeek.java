package pen.calendars;

import java.util.List;

/**
 * The days of a normal week.
 */
public class StandardWeek {
    public static final Weekday SUNDAY =
        new Weekday("Sunday", "Sun", "Su", "S");
    public static final Weekday MONDAY =
        new Weekday("Monday", "Mon", "M", "M");
    public static final Weekday TUESDAY =
        new Weekday("Tuesday", "Tue", "Tu", "T");
    public static final Weekday WEDNESDAY =
        new Weekday("Wednesday", "Wed", "W", "W");
    public static final Weekday THURSDAY =
        new Weekday("Thursday", "Thu", "Th", "T");
    public static final Weekday FRIDAY =
        new Weekday("Friday", "Fri", "F", "F");
    public static final Weekday SATURDAY =
        new Weekday("Saturday", "Sat", "Sa", "S");

    /**
     * An unmodifiable list of the standard weekdays.
     */
    public static final List<Weekday> DAYS = List.of(
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    );
}
