package pen.history;

import pen.calendars.Calendar;
import pen.calendars.formatter.DateFormat;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link HistoryBank} or {@link HistoryQuery}.
 */
public interface History {
    //-------------------------------------------------------------------------
    // Public Methods

    Optional<Calendar> getCalendar();
    DateFormat getDateFormat();


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

    TimeFrame getTimeFrame();

    Map<String,Period> getPeriods(TimeFrame frame);

    /**
     * A text timeline chart for the incidents and entities in the history.
     * @return The chart.
     */
    String toTimelineChart();
}
