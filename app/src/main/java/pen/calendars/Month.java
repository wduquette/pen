package pen.calendars;

public interface Month extends CalendarName {
    @Override
    String toString();

    String fullForm();

    String shortForm();

    String unambiguousForm();

    String tinyForm();
}
