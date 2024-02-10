package pen.calendars;

/**
 * A date in a particular calendar
 * @param year The year number, omitting 0
 * @param monthOfYear The month number, counting from 1
 * @param dayOfMonth The day of the month, counting from 1
 */
public record YearMonthDay(
    int year,
    int monthOfYear,
    int dayOfMonth
) {
}
