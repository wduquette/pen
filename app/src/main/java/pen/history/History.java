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
    /**
     * The calendar used to format dates in printed output.
     * @return the calendar
     */
    Optional<Calendar> getCalendar();

    /**
     * The format for printed dates.
     * @return The format
     */
    DateFormat getDateFormat();

    /**
     * The entities in the history, by name.
     * TODO: Consider exposing a list rather than the map.
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
