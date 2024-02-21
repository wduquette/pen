package pen.calendars;

/**
 * A month in a {@link Calendar}.
 */
public interface Month extends CalendarName {
    String name();

    @Override
    default String tinyForm() {
        return fullForm().substring(0,1);
    }

    @Override
    default String unambiguousForm() {
        return fullForm().substring(0,3);
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
