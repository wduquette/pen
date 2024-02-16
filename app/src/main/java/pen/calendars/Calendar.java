package pen.calendars;

/**
 * A calendar for general use.
 */
public interface Calendar {
    /**
     * The number of days in the given year, per the calendar
     * @param year The year
     * @return The number of days
     */
    int daysInYear(int year);

    /**
     * Formats the fundamental day as a date string for the given calendar.
     * @param day The fundamental day
     * @return The string
     */
    String formatDate(int day);

    /**
     * Parses the date string, returning a fundamental day.
     * @param dateString The date string
     * @return The fundamental day
     * @throws CalendarException on parse error
     */
    int parseDate(String dateString);

    /**
     * Gets the calendar's weekly cycle, if it has one.
     * @return The Week, or null.
     */
    default Week week() {
        return null;
    }

    /**
     * Gets whether the calendar has a weekly cycle or not.
     * @return true or false
     */
    default boolean hasWeeks() {
        return false;
    }

    /**
     * Produces the weekday for the given fundamental day.
     * @param day The day
     * @return The weekday
     * @throws UnsupportedOperationException if this calendar lacks a
     * weekly cycle.
     */
    default Weekday day2weekday(int day) {
        throw new UnsupportedOperationException(
            "Calendar lacks a weekly cycle.");
    }
}
