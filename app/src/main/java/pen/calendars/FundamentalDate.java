package pen.calendars;

/**
 * A date relative to a specific fundamental calendar.
 * @param year The year number
 * @param dayOfYear The day of year, counting from 1.
 */
public record FundamentalDate(
    int year,
    int dayOfYear
) {
    @Override
    public String toString() {
        return "FD" + year + "/" + dayOfYear;
    }
}
