package pen.calendars;

/**
 * The three ways to present a day, month, or era name.
 */
public interface DateText {
    /**
     * The narrow form, typically one character.
     * @return The narrow form
     */
    String narrowForm();

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
}
