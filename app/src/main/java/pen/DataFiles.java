package pen;

import pen.apis.CalendarExtension;
import pen.apis.HistoryExtension;
import pen.calendars.Calendar;
import pen.tcl.TclEngine;
import pen.tcl.TclEngineException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        // Set the working directory so that all paths in the engine are
        // relative to the file being loaded.
        engine.setWorkingDirectory(path.toAbsolutePath().getParent());
        var calendarExtension = new CalendarExtension();
        engine.install(calendarExtension);

        try {
            var script = Files.readString(path);
            engine.eval(script);
        } catch (Exception ex) {
            throw error("calendar", ex);
        }

        return calendarExtension.getCalendars();
    }

    /**
     * Loads a history file, and returns the loaded history.
     * @param path The path to the file
     * @return The history file's content
     * @throws DataFileException on error
     */
    public static HistoryFile loadHistory(Path path)
        throws DataFileException
    {
        var engine = new TclEngine();
        // Set the working directory so that all paths in the engine are
        // relative to the file being loaded.
        engine.setWorkingDirectory(path.toAbsolutePath().getParent());
        var historyExtension = new HistoryExtension();
        engine.install(historyExtension);

        try {
            var script = Files.readString(path);
            engine.eval(script);
        } catch (Exception ex) {
            throw error("history", ex);
        }

        return new HistoryFile(
            path,
            historyExtension.getHistory(),
            historyExtension.getQuery(),
            historyExtension.getCalendar().orElse(null)
        );
    }

    private static DataFileException error(String what, Exception ex) {
        return switch (ex) {
            case IOException ignored ->
                new DataFileException(
                    "Error reading " + what + ", " +ex. getMessage(), ex);
            case TclEngineException ignored ->
                new DataFileException(
                    "Error in " + what + ", " + ex.getMessage(), ex);
            default ->
                new DataFileException(
                    "Unexpected error while loading " + what + ", " +
                    ex.getMessage(), ex);
        };
    }
}
