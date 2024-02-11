package pen.calendars;

import java.util.List;

/**
 * Defines a week given some number of weekday objects.  The objects
 * must all be of the same type.  In Java code, W might be an Enum type;
 * for a Week created by a script they might be something else.
 * @param weekdays The weekday objects
 * @param offset The offset for day 0 on the FundamentalCalendar.
 */
public record Week(List<Weekday> weekdays, int offset) {
    /**
     * Converts a fundamental calendar day into the matching weekday.
     * @param day The day
     * @return The weekday object
     */
    public Weekday day2weekday(int day) {
        int ndx = (day + offset) % weekdays.size();
        return weekdays.get(ndx);
    }

    /**
     * Gets the index of the weekday.
     * @param weekday The weekday
     * @return The index, or -1 if not found.
     */
    public int indexOf(Weekday weekday) {
        return weekdays.indexOf(weekday);
    }
}
