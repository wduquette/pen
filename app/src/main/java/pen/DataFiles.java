package pen;

import pen.apis.HistoryExtension;
import pen.history.HistoryBank;
import pen.tcl.TclEngine;
import pen.tcl.TclEngineException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Static class for loading data formats.
 */
public class DataFiles {
    private DataFiles() {} // Not instantiable

    public static HistoryBank loadHistory(Path path)
        throws DataFileException
    {
        var engine = new TclEngine();
        var historyExtension = new HistoryExtension();
        engine.install(historyExtension);

        try {
            var script = Files.readString(path);
            engine.evalFile(path.toFile());
        } catch (IOException ex) {
            throw new DataFileException("Error reading history", ex);
        } catch (TclEngineException ex) {
            throw new DataFileException("Error in history", ex);
        } catch (Exception ex) {
            throw new DataFileException("Unexpected error while loading history", ex);
        }

        return historyExtension.getHistory();
    }
}
