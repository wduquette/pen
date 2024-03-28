package pen;

import pen.apis.CalendarExtension;
import pen.apis.HistoryExtension;
import pen.calendars.Calendar;
import pen.history.HistoryBank;
import pen.tcl.TclEngine;
import pen.tcl.TclEngineException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Static class for loading data formats.
 */
public class DataFiles {
    private DataFiles() {} // Not instantiable

    /**
     * Loads a calendar file, and returns a map containing the loaded
     * calendars.
     * @param path The path to the file
     * @return The map
     * @throws DataFileException on error
     */
    public static Map<String,Calendar> loadCalendar(Path path)
        throws DataFileException
    {
        var engine = new TclEngine();
        var calendarExtension = new CalendarExtension();
        engine.install(calendarExtension);

        try {
            var script = Files.readString(path);
            engine.eval(script);
        } catch (Exception ex) {
            throw error("history", ex);
        }

        return calendarExtension.getCalendars();
    }

    /**
     * Loads a history file, and returns the loaded history.
     * @param path The path to the file
     * @return The history
     * @throws DataFileException on error
     */
    public static HistoryBank loadHistory(Path path)
        throws DataFileException
    {
        var engine = new TclEngine();
        var historyExtension = new HistoryExtension();
        engine.install(historyExtension);

        try {
            var script = Files.readString(path);
            engine.eval(script);
        } catch (Exception ex) {
            throw error("history", ex);
        }

        return historyExtension.getHistory();
    }

    private static DataFileException error(String what, Exception ex) {
        return switch (ex) {
            case IOException e ->
                new DataFileException("Error reading " + what, ex);
            case TclEngineException e ->
                new DataFileException("Error in " + what, ex);
            default ->
                new DataFileException(
                    "Unexpected error while loading " + what, ex);
        };
    }
}