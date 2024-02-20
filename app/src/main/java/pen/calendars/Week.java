package pen.calendars;

import java.util.List;

/**
 * Defines a week given some number of {@link Weekday} objects.
 * @param weekdays The weekday objects
 * @param epochOffset The offset for the weekday of the epoch.
 */
public record Week(List<Weekday> weekdays, int epochOffset) {
    /**
     * Converts an epoch day into the matching weekday.
     * @param day The day
     * @return The weekday object
     */
    public Weekday day2weekday(int day) {
        int ndx = (day + epochOffset) % weekdays.size();
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
