package pen.calendars;

/**
 * A date relative to a specific calendar.
 * @param year The year number, omitting 0.
 * @param dayOfYear The day of year, counting from 1.
 */
public record YearDayOfYear(
    Calendar calendar,
    int year,
    int dayOfYear
) {
    @Override
    public String toString() {
        return "FD" + year + "/" + dayOfYear;
    }
}
