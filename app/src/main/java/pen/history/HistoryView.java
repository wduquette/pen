package pen.history;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class HistoryView
    extends AbstractHistory implements History
{
    //-------------------------------------------------------------------------
    // Constructor

    public HistoryView(
        Map<String,Entity> entityMap,
        List<Incident> incidents
    ) {
        setEntityMap(entityMap);
        setIncidents(incidents);
    }

    /**
     * Makes a copy of the given history.
     * @param history The history
     */
    public HistoryView(History history) {
        setMomentFormatter(history.getMomentFormatter());
        setEntityMap(history.getEntityMap());
        setIncidents(history.getIncidents());
    }

    //-------------------------------------------------------------------------
    // Provide unmodifiable access to data

    @Override
    public Map<String, Entity> getEntityMap() {
        return Collections.unmodifiableMap(entityMap());
    }

    @Override
    public List<Incident> getIncidents() {
        return Collections.unmodifiableList(incidents());
    }



    @Override
    public TimeFrame getTimeFrame() {
        // TODO: Move implementation to AbstractHistory
        return null;
    }

    @Override
    public Map<String, Period> getPeriods(TimeFrame frame) {
        // TODO: Move implementation to AbstractHistory
        return null;
    }

    @Override
    public String toTimelineChart() {
        // TODO: Move implementation to AbstractHistory
        return null;
    }
}
