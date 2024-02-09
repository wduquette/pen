package pen.calendars;

/**
 * A date in a particular SimpleCalendar.
 * @param year The year number, counting from 1 or -1
 * @param monthOfYear The month number, counting from 1
 * @param dayOfMonth The day of the month, counting from 1
 */
public record SimpleDate(
    int year,
    int monthOfYear,
    int dayOfMonth
) {
}
