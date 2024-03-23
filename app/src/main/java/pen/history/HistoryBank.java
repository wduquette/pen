package pen.history;

import pen.calendars.Calendar;
import pen.calendars.BasicCalendar;
import pen.calendars.formatter.DateFormat;
import pen.util.TextCanvas;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class HistoryBank
    extends AbstractHistory implements History
{
    //-------------------------------------------------------------------------
    // Constructor

    public HistoryBank() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // Accessors


    @Override
    public void setMomentFormatter(Function<Integer,String> formatter) {
        super.setMomentFormatter(formatter);
    }

    public Function<Integer,String> getMomentFormatter() {
        return super.momentFormatter();
    }

    public Map<String,Entity> getEntityMap() {
        return entityMap();
    }

    public void addEntity(Entity entity) {
        entityMap().put(entity.id(), entity);
    }

    public Optional<Entity> removeEntity(String id) {
        return Optional.ofNullable(entityMap().remove(id));
    }

    public Optional<Entity> getEntity(String id) {
        return Optional.ofNullable(entityMap().get(id));
    }

    @Override
    public List<Incident> getIncidents() {
        return incidents();
    }

    /**
     * Returns the time frame given all events.
     * @return the time frame.
     */
    public TimeFrame getTimeFrame() {
        return getTimeFrame(incident -> true);
    }

    public TimeFrame getTimeFrame(Predicate<Incident> filter) {
        var filtered = incidents().stream()
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
        var entity = entityMap().get(entityId);
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

        for (var id : entityMap().keySet()) {
            getPeriod(id, frame).ifPresent(period -> map.put(id, period));
        }

        return map;
    }

    public List<Incident> getIncidents(String entityId) {
        return incidents().stream()
            .filter(i -> i.concerns(entityId))
            .sorted(Comparator.comparing(Incident::moment))
            .toList();
    }

    private static final String INCIDENTS = "Incidents";
    private static final String H_LINE = TextCanvas.LIGHT_HORIZONTAL;
    private static final String V_LINE = TextCanvas.LIGHT_VERTICAL;
    private static final String HARD_START = TextCanvas.LIGHT_DOWN_AND_HORIZONTAL;
    private static final String HARD_END = TextCanvas.LIGHT_UP_AND_HORIZONTAL;
    private static final String SOFT_START = TextCanvas.WHITE_UP_POINTING_TRIANGLE;
    private static final String SOFT_END = TextCanvas.WHITE_DOWN_POINTING_TRIANGLE;
    private static final String CONCERNED = TextCanvas.LIGHT_VERTICAL_AND_LEFT;

    @Override
    public String toTimelineChart() {
        // FIRST, get the data
        var entities = new ArrayList<>(getEntityMap().values());
        var sortedIncidents = incidents().stream()
            .sorted(Comparator.comparing(Incident::moment))
            .toList();
        var frame = getTimeFrame();
        var periods = getPeriods(frame);
        assert entities.size() == periods.size();

        // NEXT, get the width of the incident labels.
        var labelWidth = incidents().stream()
            .mapToInt(i -> i.label().length())
            .max().orElse(0);
        labelWidth = Math.max(labelWidth, INCIDENTS.length());

        // NEXT, compute coordinates
        var c0 = labelWidth + 2;      // C coordinate of the body
        var r0 = entities.size() + 2; // R coordinate of the body

        // NEXT, plot the header
        var canvas = new TextCanvas();

        for (var i = 0; i < entities.size(); i++) {
            var c = c0 + i*3;
            var r = i;
            canvas.puts(c - 1, r, getEntityLabel(entities.get(i)));
            for (var rLine = r + 1; rLine < r0 - 1; rLine++) {
                canvas.puts(c, rLine, TextCanvas.LIGHT_VERTICAL);
            }
        }

        canvas.puts(0, r0 - 2, padLeft(INCIDENTS, labelWidth));

        // NEXT, add the separator, now that we know what the full width is.
        canvas.puts(0, r0 - 1, H_LINE.repeat(canvas.getWidth()));

        // NEXT, add a row for soft caps at the beginning, if needed.
        var t0 = sortedIncidents.getFirst().moment();
        if (periods.values().stream()
            .filter(p -> p.start() == t0)
            .anyMatch(p -> p.startCap() == Cap.SOFT)
        ) {
            ++r0;
        }

        // NEXT, add the incidents and periods
        for (var i = 0; i < sortedIncidents.size(); i++) {
            var r = r0 + i;
            var incident = sortedIncidents.get(i);
            var t = incident.moment();

                // FIRST, add the incident
            canvas.puts(0, r, padLeft(incident.label(), labelWidth));

            // NEXT, add the periods.
            for (var j = 0; j < entities.size(); j++) {
                var entity = entities.get(j);
                var period = periods.get(entity.id());
                var concerned = incident.concerns(entity.id());
                var c = c0 + 3*j;

                if (concerned) {
                    canvas.puts(c - 1, r, H_LINE);
                }

                if (t < period.start() || t > period.end()) {
                    // Do nothing
                } else if (period.start() == t) {
                    if (period.startCap() == Cap.HARD) {
                        canvas.puts(c, r, HARD_START);
                    } else {
                        canvas.puts(c, r - 1, SOFT_START);
                        canvas.puts(c, r, concerned ? CONCERNED : V_LINE);
                    }
                } else if (period.end() == t) {
                    if (period.endCap() == Cap.HARD) {
                        canvas.puts(c, r, HARD_END);
                    } else {
                        canvas.puts(c, r, concerned ? CONCERNED : V_LINE);
                        canvas.puts(c, r + 1, SOFT_END);
                    }
                } else if (concerned) {
                    canvas.puts(c, r, CONCERNED);
                } else {
                    canvas.puts(c, r, V_LINE);
                }
            }
        }

        return canvas.toString();
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

