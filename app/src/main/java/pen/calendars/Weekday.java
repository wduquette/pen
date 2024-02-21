package pen.calendars;

public interface Weekday extends CalendarName {
    /**
     * The day's full name, usually in mixed case.
     * @return The full name.
     */
    String name();

    @Override
    default String tinyForm() {
        return fullForm().substring(0,1);
    }

    @Override
    default String unambiguousForm() {
        return fullForm().substring(0,2);
    }

    @Override
    default String shortForm() {
        return fullForm().substring(0,3);
    }

    @Override
    default String fullForm() {
        return name();
    }
}
