package pen.history;

import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class History {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The entities in this diagram.  Use a LinkedHashMap to preserve
    private final SequencedMap<String,Entity> entityMap =
        new LinkedHashMap<>();

    // The incidents
    private final List<Incident> incidents = new ArrayList<>();

    //-------------------------------------------------------------------------
    // Constructor

    public History() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // Accessors

    public Map<String,Entity> getEntityMap() {
        return entityMap;
    }

    public void addEntity(Entity entity) {
        entityMap.put(entity.id(), entity);
    }

    public Optional<Entity> removeEntity(String id) {
        return Optional.ofNullable(entityMap.remove(id));
    }

    public Optional<Entity> getEntity(String id) {
        return Optional.ofNullable(entityMap.get(id));
    }

    public List<Incident> getIncidents() {
        return incidents;
    }

    /**
     * Returns the time frame given all events.
     * @returns the time frame.
     */
    public TimeFrame getTimeFrame() {
        return getTimeFrame(incident -> true);
    }

    public TimeFrame getTimeFrame(Predicate<Incident> filter) {
        var filtered = incidents.stream()
            .filter(filter::test)
            .toList();

        var start = filtered.stream()
            .mapToInt(Incident::moment)
            .min().orElse(0);
        var end = filtered.stream()
            .mapToInt(Incident::moment)
            .max().orElse(0);

        return new TimeFrame(start, end);
    }

    public Optional<Period> getPeriod(String entityId) {
        return getPeriod(entityId, getTimeFrame());
    }

    public Optional<Period> getPeriod(String entityId, TimeFrame frame) {
        // FIRST, get the entity
        var entity = entityMap.get(entityId);
        if (entity == null) {
            throw new IllegalArgumentException("No such entity: \"" + entityId + "\"");
        }

        // NEXT, get the sorted earliest and last incidents concerning this
        // entity
        var all = getIncidents(entityId);

        if (all.isEmpty()) {
            return Optional.empty();
        }

        var first = all.getFirst();
        var last = all.getLast();

        var start = all.stream()
            .filter(i -> i.moment() >= frame.start())
            .findFirst();
        var end = all.stream()
            .filter(i -> i.moment() <= frame.end())
            .findFirst();

        if (!start.isPresent() || !end.isPresent()) {
            return Optional.empty();
        }

        var startCap = (start.get().moment() > first.moment())
            ? start.get().cap()
            : Cap.SOFT;  // The period begins before the frame
        var endCap = (end.get().moment() < last.moment())
            ? end.get().cap()
            : Cap.SOFT;  // The period ends after the frame

        return Optional.of(new Period(
            entity,
            start.get().moment(),
            end.get().moment(),
            startCap,
            endCap
        ));
    }

    public List<Incident> getIncidents(String entityId) {
        return incidents.stream()
            .filter(i -> i.concerns(entityId))
            .sorted(Comparator.comparing(Incident::moment))
            .toList();
    }
}

