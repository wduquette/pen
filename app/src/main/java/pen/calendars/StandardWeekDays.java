package pen.calendars;

import java.util.List;

/**
 * The days of a normal week.
 */
public enum StandardWeekDays implements Weekday {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY;

    /**
     * An unmodifiable list of the weekdays.
     * @return The list
     */
    public static List<Weekday> weekdays() {
        return List.of(values());
    }
}
