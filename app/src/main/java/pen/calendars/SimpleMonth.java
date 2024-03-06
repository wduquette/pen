package pen.calendars;

/**
 * A month in a {@link Calendar}.
 */
public record SimpleMonth(
    String fullForm,
    String shortForm,
    String unambiguousForm,
    String tinyForm
) implements Month {
    @Override
    public String toString() {
        return "Month(" + fullForm + ")";
    }
}
