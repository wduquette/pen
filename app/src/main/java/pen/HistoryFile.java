package pen;

import pen.calendars.Calendar;
import pen.history.HistoryBank;
import pen.history.HistoryQuery;

import java.nio.file.Path;

/**
 * Data loaded from a .hist file
 * @param path The file's path
 * @param history The history itself
 * @param query The default query
 * @param calendarFile The calendars associated with the history
 * @param primaryCalendar The name of the primary calendar for this history
 */
public record HistoryFile(
    Path path,
    HistoryBank history,
    HistoryQuery query,
    CalendarFile calendarFile,
    String primaryCalendar
) {
    public Calendar getPrimaryCalendar() {
        return calendarFile.calendars().get(primaryCalendar);
    }
}
