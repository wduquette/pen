package pen.calendars;

/**
 * A (year, day-of-year) relative to a specific {@link Calendar}.  Use
 * {@code Calendar::validate} to validate the year and day of year with
 * respect to the calendar.
 *
 * @param calendar The calendar in question
 * @param year The year number, omitting 0.
 * @param dayOfYear The day of year, counting from 1.
 */
public record YearDay(
    Calendar calendar,
    int year,
    int dayOfYear
) {
    @Override
    public String toString() {
        return calendar + ":" + year + "/" + dayOfYear;
    }
}
