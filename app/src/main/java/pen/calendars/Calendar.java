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
}
