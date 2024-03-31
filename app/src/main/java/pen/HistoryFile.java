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
 * @param calendar The associated calendar, or null
 */
public record HistoryFile(
    Path path,
    HistoryBank history,
    HistoryQuery query,
    Calendar calendar
) {
}
