package pen.history;

import java.util.*;
import java.util.function.Function;

public class AbstractHistory {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The calendar information
    private Function<Integer, String> momentFormatter;

    // The entities in this diagram.  Use a LinkedHashMap to preserve
    private final SequencedMap<String, Entity> entityMap =
        new LinkedHashMap<>();

    // The incidents
    private final List<Incident> incidents = new ArrayList<>();

    //-------------------------------------------------------------------------
    // Constructors

    public AbstractHistory() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // Protected Members, for use by subclasses.
    //
    // Subclasses are free to edit and view the history as they like.

    protected final Function<Integer, String> momentFormatter() {
        return momentFormatter;
    }

    protected void setMomentFormatter(Function<Integer, String> formatter) {
        this.momentFormatter = formatter;
    }

    protected final SequencedMap<String, Entity> entityMap() {
        return entityMap;
    }

    protected final void setEntityMap(Map<String, Entity> map) {
        entityMap.clear();
        entityMap.putAll(map);
    }

    protected final List<Incident> incidents() {
        return incidents;
    }

    protected final void setIncidents(List<Incident> list) {
        incidents.clear();
        incidents.addAll(list);
    }

    //-------------------------------------------------------------------------
    // Standard Queries.
    //
    // These queries are available to all subclasses.


}
