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
        Map<String,EntityType> typeMap,
        Map<String,Entity> entityMap,
        List<Incident> incidents,
        LinkedHashMap<String, List<Period>> periodGroups
    ) {
        this.periodGroups = periodGroups;
        setTypeMap(typeMap);
        setEntityMap(entityMap);
        setIncidents(incidents);
    }

    /**
     * Makes a copy of the given history.
     * @param history The history
     */
    @SuppressWarnings("unused")
    public HistoryView(History history) {
        setMomentFormatter(history.getMomentFormatter());
        setEntityMap(history.getEntityMap());
        setIncidents(history.getIncidents());

        this.periodGroups = history.getPeriodGroups();
    }

    //-------------------------------------------------------------------------
    // Provide unmodifiable access to data

    @Override
    public Map<String, EntityType> getTypeMap() {
        return Collections.unmodifiableMap(typeMap());
    }

    @Override
    public Map<String, Entity> getEntityMap() {
        return Collections.unmodifiableMap(entityMap());
    }

    @Override
    public List<Incident> getIncidents() {
        return incidents().stream()
            .sorted(Comparator.comparing(Incident::moment))
            .toList();
    }

    @Override
    public LinkedHashMap<String,List<Period>> getPeriodGroups() {
        return periodGroups;
    }
}
