package pen.calendars;

/**
 * The three ways to present a day, month, or era name.
 */
public interface CalendarName {
    /**
     * The smallest form, typically one character.
     * @return The narrow form
     */
    String tinyForm();

    /**
     * The smallest unambiguous form.
     * @return The narrow form
     */
    String unambiguousForm();

    /**
     * The short form, typically three characters with initial cap.
     * @return The short form
     */
    String shortForm();

    /**
     * The full form: the entire name, typically mixed case.
     * @return The name
     */
    String fullForm();

    /**
     * Gets the selected form of the name.
     * @param form The form
     * @return The name
     */
    default String getForm(Form form) {
        return switch (form) {
            case FULL -> fullForm();
            case SHORT -> shortForm();
            case UNAMBIGUOUS -> unambiguousForm();
            case TINY -> tinyForm();
        };
    }
}
