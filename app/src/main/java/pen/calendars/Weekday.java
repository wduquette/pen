package pen.calendars;

public interface Weekday extends DateText {
    /**
     * The day's full name, usually in mixed case.
     * @return The full name.
     */
    String name();

    @Override
    default String narrowForm() {
        return name().substring(0,1);
    }

    @Override
    default String shortForm() {
        return name().substring(0,3);
    }

    @Override
    default String fullForm() {
        return name();
    }
}
