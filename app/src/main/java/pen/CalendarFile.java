package pen;

import pen.calendars.Calendar;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Data loaded from a .cal file
 * @param path The file's path
 * @param calendars The loaded calendars
 * @param today The date to show by default
 */
public record CalendarFile(
    Path path,
    LinkedHashMap<String,Calendar> calendars,
    int today
) {
    /**
     * Gets a list of the names of the calendars defined in the file.
     * @return The names
     */
    public List<String> getNames() {
        return new ArrayList<>(calendars.keySet());
    }
}
