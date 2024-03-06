package pen.calendars;

/**
 * A month in a {@link BasicCalendar}.
 */
public record BoundedMonth(
    String fullForm,
    String shortForm,
    String unambiguousForm,
    String tinyForm,
    YearDelta daysInMonth
) implements Month {
    public static BoundedMonth of(Month month, YearDelta daysInMonth) {
        return new BoundedMonth(
            month.fullForm(),
            month.shortForm(),
            month.unambiguousForm(),
            month.tinyForm(),
            daysInMonth
        );
    }

    @Override
    public String toString() {
        return "BoundedMonth(" + fullForm + ")";
    }
}
