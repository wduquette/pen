package pen.history;

import java.util.*;

public class HistoryView
    extends AbstractHistory implements History
{
    //-------------------------------------------------------------------------
    // Instance Variables

    private final LinkedHashMap<String, List<Period>> periodGroups;

    //-------------------------------------------------------------------------
    // Constructor

    public HistoryView(
        Map<String,Entity> entityMap,
        List<Incident> incidents, LinkedHashMap<String, List<Period>> periodGroups
    ) {
        this.periodGroups = periodGroups;
        setIncidents(incidents);
        setEntityMap(entityMap);
    }

    /**
     * Makes a copy of the given history.
     * @param history The history
     */
    public HistoryView(History history) {
        setMomentFormatter(history.getMomentFormatter());
        setEntityMap(history.getEntityMap());
        setIncidents(history.getIncidents());

        this.periodGroups = history.getPeriodGroups();
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
    public LinkedHashMap<String,List<Period>> getPeriodGroups() {
        return periodGroups;
    }
}
