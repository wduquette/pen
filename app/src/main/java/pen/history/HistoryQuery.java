package pen.history;

import java.util.*;
import java.util.function.Predicate;

public class HistoryQuery {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The query terms; they will be executed in order by execute().
    private final List<Term> terms = new ArrayList<>();

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates an empty query.
     */
    public HistoryQuery() {
    }

    //-------------------------------------------------------------------------
    // Term Definitions

    // The query terms. See the javadoc for the term methods for semantics.
    private interface Term {
        record IncidentFilter(Predicate<Incident> filter) implements Term {}
        record Excludes(List<String> entityIds) implements Term {}
        record Includes(List<String> entityIds) implements Term {}
        record IncludesTypes(List<String> types) implements Term {}
        record ExcludesTypes(List<String> types) implements Term {}
        record BoundBy(List<String> entityIds) implements Term {}
    }

    //-------------------------------------------------------------------------
    // Terms

    /**
     * Clears all query terms.  The query will return the entire history.
     * @return The query
     */
    public HistoryQuery clear() {
        terms.clear();
        return this;
    }

    /**
     * Filters out all incidents prior to the given moment.
     * @param moment The moment
     * @return The query
     */
    public HistoryQuery noEarlierThan(int moment) {
        terms.add(new Term.IncidentFilter(in -> in.moment() >= moment));
        return this;
    }

    /**
     * Filters out all incidents following the given moment.
     * @param moment The moment
     * @return The query
     */
    public HistoryQuery noLaterThan(int moment) {
        terms.add(new Term.IncidentFilter(in -> in.moment() <= moment));
        return this;
    }

    /**
     * A general filter for incidents.  Only incidents for which the
     * predicate is true will be included.
     * @param predicate The predicate
     * @return The query
     */
    public HistoryQuery filter(Predicate<Incident> predicate) {
        terms.add(new Term.IncidentFilter(predicate));
        return this;
    }

    /**
     * The query includes all entities by default. If no inclusions or
     * exclusions have been done, this limits the set of entities to
     * those given.  Otherwise, these entities are added to the set.
     * @param entityIds The entity IDs
     * @return The query
     */
    public HistoryQuery includes(String... entityIds) {
        return includes(List.of(entityIds));
    }

    /**
     * The query includes all entities by default. If no inclusions or
     * exclusions have been done, this limits the set of entities to
     * those given.  Otherwise, these entities are added to the set.
     * @param entityIds The entity IDs
     * @return The query
     */
    public HistoryQuery includes(List<String> entityIds) {
        terms.add(new Term.Includes(new ArrayList<>(entityIds)));
        return this;
    }

    /**
     * The query includes all entities by default.  If this is found, the
     * named entities are removed from the set of included entities.
     * @param entityIds The entity IDs
     * @return The query
     */
    public HistoryQuery excludes(String... entityIds) {
        return excludes(List.of(entityIds));
    }

    /**
     * The query includes all entities by default.  If this is found, the
     * named entities are removed from the set of included entities.
     * @param entityIds The entity IDs
     * @return The query
     */
    public HistoryQuery excludes(List<String> entityIds) {
        terms.add(new Term.Excludes(new ArrayList<>(entityIds)));
        return this;
    }

    /**
     * The query includes all entities by default. If no inclusions or
     * exclusions have been done, this limits the set of entities to
     * those having the types given. Otherwise, entities of these types are
     * added to the set.
     * @param types The entity types
     * @return The query
     */
    public HistoryQuery includeTypes(String... types) {
        return includeTypes(List.of(types));
    }

    /**
     * The query includes all entities by default. If no inclusions or
     * exclusions have been done, this limits the set of entities to
     * those having the types given. Otherwise, entities of these types are
     * added to the set.
     * @param types The entity types
     * @return The query
     */
    public HistoryQuery includeTypes(List<String> types) {
        terms.add(new Term.IncludesTypes(types));
        return this;
    }

    /**
     * The query includes all entities by default.  If this is found, entities
     * having the named types are removed from the set of included entities.
     * @param types The types to exclude
     * @return The query
     */
    public HistoryQuery excludeTypes(String... types) {
        return excludeTypes(List.of(types));
    }

    /**
     * The query includes all entities by default.  If this is found, entities
     * having the named types are removed from the set of included entities.
     * @param types The types to exclude
     * @return The query
     */
    public HistoryQuery excludeTypes(List<String> types) {
        terms.add(new Term.ExcludesTypes(types));
        return this;
    }

    /**
     * This term limits the time range to the incidents that concern the
     * named entities.  If no entities are listed here, the time range is
     * limited to the incidents that concern all included entities.
     * @param entityIds The entities of interest
     * @return The query
     */
    public HistoryQuery boundByEntities(String... entityIds) {
        return boundByEntities(List.of(entityIds));
    }

    /**
     * This term limits the time range to the incidents that concern the
     * named entities.  If no entities are listed here, the time range is
     * limited to the incidents that concern all included entities.
     * @param entityIds The entities of interest
     * @return The query
     */
    public HistoryQuery boundByEntities(List<String> entityIds) {
        terms.add(new Term.BoundBy(entityIds));
        return this;
    }

    //------------------------------------------------------------------------
    // Query

    /**
     * Executes the query by executing the query terms in order for the
     * source history, and returns a history that reflects the executed query.
     * Each query time might:
     *
     * <ul>
     * <li>Modify the set of incidents to include</li>
     * <li>Modify the set of entities to include</li>
     * </ul>
     * @param source The source history
     * @return The resulting history
     */
    public History execute(History source) {
        var incidents = source.getIncidents().stream()
            .sorted(Comparator.comparing(Incident::moment))
            .toList();
        var entities = new HashSet<>(source.getEntityMap().keySet());
        var periods = source.getPeriods(source.getTimeFrame());
        var modified = false;

        for (var term : terms) {
            switch (term) {
                case Term.IncidentFilter t ->
                    incidents = incidents.stream().filter(t.filter).toList();
                case Term.Includes t -> {
                    if (!modified) {
                        entities.clear();
                    }
                    modified = true;
                    entities.addAll(t.entityIds);
                }
                case Term.IncludesTypes t -> {
                    if (!modified) {
                        entities.clear();
                    }
                    modified = true;
                    var toInclude = periods.values().stream()
                        .map(Period::entity)
                        .filter(e -> t.types().contains(e.type()))
                        .map(Entity::id)
                        .toList();
                    entities.addAll(toInclude);
                }
                case Term.Excludes t -> {
                    modified = true;
                    t.entityIds().forEach(entities::remove);
                }
                case Term.ExcludesTypes t -> {
                    modified = true;
                    var toRetain = periods.values().stream()
                        .map(Period::entity)
                        .filter(e -> !t.types().contains(e.type()))
                        .map(Entity::name)
                        .toList();
                    entities = new HashSet<>(toRetain);
                }
                case Term.BoundBy t -> {
                    var list = new ArrayList<Period>();

                    var ids = !t.entityIds.isEmpty() ? t.entityIds : entities;

                    for (var id : ids) {
                        if (periods.containsKey(id)) {
                            list.add(periods.get(id));
                        }
                    }

                    var start = list.stream()
                        .mapToInt(Period::start)
                        .min().orElse(Integer.MIN_VALUE);
                    var end = list.stream()
                        .mapToInt(Period::end)
                        .min().orElse(Integer.MAX_VALUE);
                    incidents = incidents.stream()
                        .filter(in -> in.moment() >= start && in.moment() <= end)
                        .toList();
                }
                default ->
                    throw new IllegalStateException(
                        "Unknown term:" + term);
            }
        }

        // NEXT, produce the resulting view
        Map<String,Entity> map = new LinkedHashMap<>();
        entities.forEach(id -> map.put(id, periods.get(id).entity()));

        var result = new HistoryView(map, incidents);
        result.setMomentFormatter(source.getMomentFormatter());

        return result;
    }

}
