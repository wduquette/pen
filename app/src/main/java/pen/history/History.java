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
     * @return the time frame.
     */
    public TimeFrame getTimeFrame() {
        return getTimeFrame(incident -> true);
    }

    public TimeFrame getTimeFrame(Predicate<Incident> filter) {
        var filtered = incidents.stream()
            .filter(filter)
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

        int startMoment;
        int endMoment;
        Cap startCap;
        Cap endCap;

        if (first.moment() >= frame.end()) {
            return Optional.empty();
        } else if (first.moment() < frame.start()) {
            startMoment = frame.start();
            startCap = Cap.SOFT;
        } else {
            startMoment = first.moment();
            startCap = first.cap();
        }

        if (last.moment() <= frame.start()) {
            return Optional.empty();
        } else if (last.moment() > frame.end()) {
            endMoment = frame.end();
            endCap = Cap.SOFT;
        } else {
            endMoment = last.moment();
            endCap = last.cap();
        }

        return Optional.of(new Period(
            entity,
            startMoment,
            endMoment,
            startCap,
            endCap
        ));
    }

    public Map<String,Period> getPeriods(TimeFrame frame) {
        var map = new HashMap<String,Period>();

        for (var id : entityMap.keySet()) {
            getPeriod(id, frame).ifPresent(period -> map.put(id, period));
        }

        return map;
    }

    public List<Incident> getIncidents(String entityId) {
        return incidents.stream()
            .filter(i -> i.concerns(entityId))
            .sorted(Comparator.comparing(Incident::moment))
            .toList();
    }

    private static final String INCIDENTS = "Incidents";
    private static final String H_LINE = "\u2500";
    private static final String V_LINE = "\u2502";
    private static final String HARD_START = "\u252C";
    private static final String HARD_END = "\u2534";
    private static final String SOFT_START = "\u25B3";
    private static final String SOFT_END = "\u25BD";

    public String toTimelineChart() {
        // FIRST, get the data
        var entities = new ArrayList<>(getEntityMap().values());
        var sortedIncidents = incidents.stream()
            .sorted(Comparator.comparing(Incident::moment))
            .toList();
        var frame = getTimeFrame();
        var periods = getPeriods(frame);
        assert entities.size() == periods.size();

        // NEXT, get the width of the incident labels.
        var labelWidth = incidents.stream()
            .mapToInt(i -> i.label().length())
            .max().orElse(0);
        labelWidth = Math.max(labelWidth, INCIDENTS.length());

        // NEXT, get the width of the entity labels.
        var entityWidth = 0;
        for (var i = 0; i < entities.size(); i++) {
            var width = 3*i + getEntityLabel(entities.get(i)).length();
            entityWidth = Math.max(entityWidth, width);
        }

        // NEXT, get the width of the chart
        var chartWidth = labelWidth + entityWidth;

        // NEXT, output the header
        var buff = new StringBuilder();
        for (var i = 0; i < entities.size(); i++) {
            buff.append(padLeft("", labelWidth));

            for (var j = i; j > 0; j--) {
                buff.append("  ").append(V_LINE);
            }

            buff.append(" ")
                .append(getEntityLabel(entities.get(i)))
                .append("\n");
        }

        buff.append(padLeft("Incidents", labelWidth));
        for (var i = 0; i < entities.size(); i++) {
            buff.append("  ").append(V_LINE);
        }
        buff.append("\n");
        buff.append(H_LINE.repeat(chartWidth)).append("\n");

        // NEXT, output each row
        for (var incident : sortedIncidents) {
            var t = incident.moment();

            buff.append(padLeft(incident.label(), labelWidth));

            for (var entity : entities) {
                var period = periods.get(entity.id());
                if (t < period.start() || t > period.end()) {
                    buff.append("   ");
                } else if (period.start() == t) {
                    buff.append("  ").append(period.startCap() == Cap.HARD
                        ? HARD_START : SOFT_START);
                } else if (period.end() == t) {
                    buff.append("  ").append(period.endCap() == Cap.HARD
                        ? HARD_END : SOFT_END);
                } else {
                    buff.append("  ").append(V_LINE);
                }
            }

            buff.append("\n");
        }

        return buff.toString();
    }

    private String getEntityLabel(Entity entity) {
        return entity.name() + " (" + entity.type() + ")";
    }

    private String padLeft(String text, int width) {
        if (text.length() < width) {
            var pad = width - text.length();
            return " ".repeat(pad) + text;
        } else {
            return text;
        }
    }
}

