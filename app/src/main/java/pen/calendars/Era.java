package pen.calendars;

/**
 * A {@link Calendar} era or prior era.  The {@code UNAMBIGUOUS} and
 * {@code TINY} forms are identical to the {@code SHORT} form.
 * @param shortForm The short name, e.g., "AD"
 * @param fullForm The full name, e.g., "Anno Domini"
 */
public record Era(String shortForm, String fullForm)
    implements CalendarName
{
    public String unambiguousForm() {
        return shortForm;
    }

    public String tinyForm() {
        return shortForm;
    }
}
