package pen.calendars;

/**
 * A calendar for general use.
 */
public interface Calendar {
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
