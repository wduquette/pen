package pen.history;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        /**
         * Filters incidents according to the given predicate.
         * @param filter The predicate
         */
        record IncidentFilter(Predicate<Incident> filter) implements Term {}

        /**
         * Includes the given entities from the output.  Resets*
         the included entities list on first inclusion of entities or types.
         * @param entityIds The entity IDs to include.
         */
        record Includes(List<String> entityIds) implements Term {}

        /**
         * Excludes the given entities from the output.
         * @param entityIds The entity IDs to exclude.
         */
        record Excludes(List<String> entityIds) implements Term {}

        /**
         * Includes entities of the given types in the output.  Resets
         * the included entities list on first inclusion of entities or types.
         * @param types The entity types
         */
        record IncludesTypes(List<String> types) implements Term {}

        /**
         * Excludes entities of the given types from the output
         * @param types The entity types
         */
        record ExcludesTypes(List<String> types) implements Term {}

        /**
         * Limits the displayed time frame to that of the listed entities.
         * If the list is null, limits it to the set of included entitites.
         * @param entityIds
         */
        record BoundBy(List<String> entityIds) implements Term {}

        /**
         * Groups entities in the output by "primes": first, a group of
         * the prime entities, then a group for each of the prime types,
         * then a group for any remaining types in alphabetical order.
         * Note: entities are included in only one group.
         * @param entities The prime entities.
         * @param types The prime types.
         */
        record GroupByPrimes(List<String> entities, List<String> types)
            implements Term {}
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

    /**
     * Groups entities by prime entities and types.  If entities is non-empty,
     * any listed entities will go in the Prime group, before any other
     * entities.  This will be followed by a group for each prime type (if any)
     * followed by any remaining types in alphabetical order. Entities will
     * appear in only one group; groups that are empty will be discarded.
     * @param entityIds List of entity IDs, or null.
     * @param types List of types, or null.
     * @return The query
     */
    public HistoryQuery groupByPrimes(
        List<String> entityIds,
        List<String> types
    ) {
        terms.add(new Term.GroupByPrimes(
            entityIds != null ? entityIds : List.of(),
            types != null ? types : List.of()));
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
        return new Query(source).execute();
    }

    //-------------------------------------------------------------------------
    // Helper Types

    // Retains transient state while executing a query
    private class Query {
        //---------------------------------------------------------------------
        // Instance Variables

        final History source;
        final Map<String,Period> periods;

        Set<String> entities;
        List<Incident> incidents;
        boolean entitySetModified = false;
        Term groupingTerm = null;
        LinkedHashMap<String, List<Period>> periodGroups =
            new LinkedHashMap<>();

        //---------------------------------------------------------------------
        // Constructor

        Query(History source) {
            this.source = source;
            this.incidents = source.getIncidents().stream()
                .sorted(Comparator.comparing(Incident::moment))
                .toList();
            this.entities = new HashSet<>(source.getEntityMap().keySet());
            this.periods = source.getPeriods();
        }

        //---------------------------------------------------------------------
        // Execution

        HistoryView execute() {
            // FIRST, do the filtering
            for (var term : terms) {
                switch (term) {
                    case Term.IncidentFilter t -> filterIncidents(t);
                    case Term.Includes t -> includeEntities(t);
                    case Term.IncludesTypes t -> includeTypes(t);
                    case Term.Excludes t -> excludeEntities(t);
                    case Term.ExcludesTypes t -> excludeTypes(t);
                    case Term.BoundBy t -> boundByEntities(t);
                    case Term.GroupByPrimes t -> groupingTerm = t;
                    default ->
                        throw new IllegalStateException(
                            "Unknown term:" + term);
                }
            }

            // NEXT, compute the period groups

            if (groupingTerm != null) {
                if (groupingTerm instanceof Term.GroupByPrimes t) {
                    groupByPrimes(t);
                } else {
                    throw new IllegalStateException(
                        "Unknown term:" + groupingTerm);
                }
            } else {
                groupBySource();
            }

            // NEXT, compute the entity map
            Map<String,Entity> map = new LinkedHashMap<>();
            for (var id : entities) {
                var period = periods.get(id);
                if (period != null) {
                    map.put(id, period.entity());
                }
            }

            var result = new HistoryView(map, incidents, periodGroups);
            result.setMomentFormatter(source.getMomentFormatter());

            return result;
        }

        void filterIncidents(Term.IncidentFilter t) {
            incidents = incidents.stream().filter(t.filter).toList();
        }

        void includeEntities(Term.Includes t) {
            if (!entitySetModified) {
                entities.clear();
            }
            entitySetModified = true;
            entities.addAll(t.entityIds);
        }

        void excludeEntities(Term.Excludes t) {
            entitySetModified = true;
            t.entityIds().forEach(entities::remove);
        }

        void includeTypes(Term.IncludesTypes t) {
            if (!entitySetModified) {
                entities.clear();
            }
            entitySetModified = true;
            var toInclude = periods.values().stream()
                .map(Period::entity)
                .filter(e -> t.types().contains(e.type()))
                .map(Entity::id)
                .toList();
            entities.addAll(toInclude);
        }

        void excludeTypes(Term.ExcludesTypes t) {
            entitySetModified = true;
            var toRetain = periods.values().stream()
                .map(Period::entity)
                .filter(e -> !t.types().contains(e.type()))
                .map(Entity::name)
                .toList();
            entities = new HashSet<>(toRetain);
        }

        void boundByEntities(Term.BoundBy t) {
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

        // Get the source's period groups, but filter out the excluded
        // entities.
        void groupBySource() {
            for (var grp : source.getPeriodGroups().entrySet()) {
                var list = new ArrayList<Period>();

                for (var period : grp.getValue()) {
                    if (entities.contains(period.entity().id())) {
                        list.add(period);
                    }
                }

                if (!list.isEmpty()) {
                    periodGroups.put(grp.getKey(), list);
                }
            }
        }

        // Create period groups by primes entities and types.
        void groupByPrimes(Term.GroupByPrimes t) {
            // FIRST, get the entities and types remaining to be grouped.
            var remainingEntities = new HashSet<>(entities);
            var remainingTypes = entities.stream()
                .map(id -> source.getEntityMap().get(id).type())
                .collect(Collectors.toSet());

            // FIRST, get the prime group
            var primes = new ArrayList<Period>();
            for (var id : t.entities) {
                var period = periods.get(id);
                if (period != null) {
                    primes.add(period);
                    remainingEntities.remove(id);
                }
            }

            if (!primes.isEmpty()) {
                periodGroups.put("prime", primes);
            }

            // NEXT, add each prime type
            for (var type : t.types) {
                var group = periods.values().stream()
                    .filter(p -> p.entity().type().equals(type))
                    .filter(p -> remainingEntities.contains(p.entity().id()))
                    .sorted(Comparator.comparing(Period::start))
                    .toList();

                if (!group.isEmpty()) {
                    periodGroups.put(type, group);
                    remainingTypes.remove(type);
                }
            }

            // NEXT, add each other type
            var others = periods.values().stream()
                .map(Period::entity)
                .map(Entity::type)
                .filter(type -> remainingTypes.contains(type))
                .sorted()
                .toList();

            for (var type : others) {
                var group = periods.values().stream()
                    .filter(p -> p.entity().type().equals(type))
                    .filter(p -> remainingEntities.contains(p.entity().id()))
                    .sorted(Comparator.comparing(Period::start))
                    .toList();

                if (!group.isEmpty()) {
                    periodGroups.put(type, group);
                }
            }
        }

        List<Period> getPeriodsByType(String type) {
            return periods.values().stream()
                .filter(p -> p.entity().type().equals(type))
                .toList();
        }
    }
}
