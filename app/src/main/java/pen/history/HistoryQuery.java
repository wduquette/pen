package pen.history;

import java.util.*;
import java.util.function.Predicate;

public class HistoryQuery {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final List<Term> terms = new ArrayList<>();

    //-------------------------------------------------------------------------
    // Constructor

    public HistoryQuery() {
    }

    //-------------------------------------------------------------------------
    // Term Definitions

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

    public HistoryQuery after(int moment) {
        terms.add(new Term.IncidentFilter(in -> in.moment() >= moment));
        return this;
    }

    public HistoryQuery before(int moment) {
        terms.add(new Term.IncidentFilter(in -> in.moment() <= moment));
        return this;
    }

    public HistoryQuery includes(String... entityIds) {
        return includes(List.of(entityIds));
    }

    public HistoryQuery includes(List<String> entityIds) {
        terms.add(new Term.Includes(new ArrayList<>(entityIds)));
        return this;
    }

    public HistoryQuery excludes(String... entityIds) {
        return excludes(List.of(entityIds));
    }

    public HistoryQuery excludes(List<String> entityIds) {
        terms.add(new Term.Excludes(new ArrayList<>(entityIds)));
        return this;
    }

    public HistoryQuery includeTypes(String... types) {
        return includeTypes(List.of(types));
    }

    public HistoryQuery includeTypes(List<String> types) {
        terms.add(new Term.IncludesTypes(types));
        return this;
    }

    public HistoryQuery excludeTypes(String... types) {
        return excludeTypes(List.of(types));
    }

    public HistoryQuery excludeTypes(List<String> types) {
        terms.add(new Term.ExcludesTypes(types));
        return this;
    }

    public HistoryQuery boundByEntities(String... entityIds) {
        return boundByEntities(List.of(entityIds));
    }

    public HistoryQuery boundByEntities(List<String> entityIds) {
        terms.add(new Term.BoundBy(entityIds));
        return this;
    }

    //------------------------------------------------------------------------
    // Query

    public History query(History source) {
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
                        .map(Entity::name)
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
                    t.entityIds().forEach(id -> {
                        if (periods.containsKey(id)) {
                            list.add(periods.get(id));
                        }
                    });
                    if (list.isEmpty()) {
                        list.addAll(periods.values());
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

        var result = new HistoryBank();
        result.setMomentFormatter(source.getMomentFormatter());
        result.getIncidents().addAll(incidents);
        entities.forEach(e -> result.addEntity(periods.get(e).entity()));

        return result;
    }

}
