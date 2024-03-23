package pen.history;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A History consists of incidents, each of which may concern some number
 * of entities (persons, places, subplots).  This class provides a simple
 * read-only view of any such set.
 */
public interface History {
    //-------------------------------------------------------------------------
    // Public Methods

    /**
     * Gets the function used to format moments for display.
     * @return The function
     */
    Function<Integer,String> getMomentFormatter();

    /**
     * The entities in the history, by name.
     * @return The entities.
     */
    Map<String, Entity> getEntityMap();

    /**
     * The incidents in the history.
     * @return The incidents.
     */
    List<Incident> getIncidents();

    /**
     * Gets the TimeFrame that spans all incidents in the history.
     * @return The time frame
     */
    TimeFrame getTimeFrame();

    /**
     * Gets a map of Periods by entity ID.
     * @return The map
     */
    Map<String,Period> getPeriods();

    /**
     * A text timeline chart for the incidents and entities in the history.
     * @return The chart.
     */
    String toTimelineChart();
}
