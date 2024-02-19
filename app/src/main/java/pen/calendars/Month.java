package pen.calendars;

/**
 * A month in a calendar
 */
public interface Month extends DateText {
    String name();

    @Override
    default String narrowForm() {
        return fullForm().substring(0,1);
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
