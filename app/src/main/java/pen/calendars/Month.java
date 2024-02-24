package pen.calendars;

/**
 * A month in a {@link Calendar}.
 */
public record Month(
    String fullForm,
    String shortForm,
    String unambiguousForm,
    String tinyForm
) implements CalendarName {
    @Override
    public String toString() {
        return "Month(" + fullForm + ")";
    }
}
