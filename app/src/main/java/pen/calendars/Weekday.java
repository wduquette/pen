package pen.calendars;

public record Weekday(
    String fullForm,
    String shortForm,
    String unambiguousForm,
    String tinyForm
) implements CalendarName {
    @Override
    public String toString() {
        return "Weekday(" + fullForm + ")";
    }
}
