package pen.history;

import pen.util.TextCanvas;

import java.util.*;
import java.util.function.Function;

/**
 * Produces a Unicode timeline chart, for printing to the console, etc.
 * Requires a monospace font.
 */
public class TextTimelineChart {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final History history;
    private Function<Integer,String> momentFormatter;
    private final TextCanvas canvas = new TextCanvas();

    private final List<Incident> incidents;
    private final Map<String,Period> periods;
    private final LinkedHashMap<String,List<Period>> groups;
    private final List<Entity> entities;
    private final Map<Integer,Integer> startIndices = new HashMap<>();
    private final Map<Integer,Integer> endIndices = new HashMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    public TextTimelineChart(History history) {
        this.history = history;
        this.momentFormatter = history.getMomentFormatter();

        // FIRST, get the history.
        //
        // NOTE: For now I'm using groups just to get the order of the periods
        // and hence the order of the entities.  Later, I might want to do
        // away with the period map altogether.
        incidents = history.getIncidents().stream()
            .sorted(Comparator.comparing(Incident::moment))
            .toList();
        groups = history.getPeriodGroups();
        periods = history.getPeriods();
        entities = new ArrayList<>(groups.values().stream()
            .flatMap(List::stream)
            .map(Period::entity)
            .toList());

        entities.forEach(e -> System.out.println("entity: " + e));
        groups.forEach((k,v) -> System.out.println("group: " + v));
        periods.forEach((k,v) -> System.out.println("period: " + k + " " + v));

        // NEXT, compute the start and end incident indices for each moment.
        computeMomentIndices();
    }

    private void computeMomentIndices() {
        for (var i = 0; i < incidents.size(); i++) {
            var moment = incidents.get(i).moment();
            // Set the start index for this moment, only if we haven't seen it
            // before.
            if (!startIndices.containsKey(moment)) {
                startIndices.put(moment, i);
            }

            // Set the last index for this moment
            endIndices.put(moment, i);
        }
    }

    @SuppressWarnings("unused")
    private void dumpPeriod(Period period) {
        System.out.println(
            formatMoment(period.start()) + " to " +
            formatMoment(period.end()) + ": " +
            period.entity().id() + ":" + period.entity().type() + " " +
            period.entity().name() + " " +
            "(" + period.startCap() + "," + period.endCap() + ")"
        );
    }

    //-------------------------------------------------------------------------
    // Public Methods

    @SuppressWarnings("unused")
    public final Function<Integer, String> getMomentFormatter() {
        return momentFormatter;
    }

    @SuppressWarnings("unused")
    public void setMomentFormatter(Function<Integer, String> formatter) {
        this.momentFormatter = formatter;
    }

    //-------------------------------------------------------------------------
    // Chart Code

    private static final String INCIDENTS = "Incidents";
    private static final String H_LINE = TextCanvas.LIGHT_HORIZONTAL;
    private static final String V_LINE = TextCanvas.LIGHT_VERTICAL;
    private static final String HARD_START = TextCanvas.LIGHT_DOWN_AND_HORIZONTAL;
    private static final String HARD_END = TextCanvas.LIGHT_UP_AND_HORIZONTAL;
    private static final String SOFT_START = TextCanvas.WHITE_UP_POINTING_TRIANGLE;
    private static final String SOFT_END = TextCanvas.WHITE_DOWN_POINTING_TRIANGLE;
    private static final String CONCERNED = TextCanvas.LIGHT_VERTICAL_AND_LEFT;

    @Override
    public String toString() {
        // FIRST get the width of the incident labels.
        var labelWidth = incidents.stream()
            .mapToInt(i -> getIncidentLabel(i, 0).length())
            .max().orElse(0);
        labelWidth = Math.max(labelWidth, INCIDENTS.length());

        // NEXT, compute coordinates
        var c0 = labelWidth + 2;      // C coordinate of the body
        var r0 = entities.size() + 2; // R coordinate of the body

        // NEXT, plot the header: entities, "Incidents", and separator
        plotEntities(c0, r0);
        canvas.puts(0, r0 - 2, padLeft(INCIDENTS, labelWidth));
        canvas.puts(0, r0 - 1, H_LINE.repeat(canvas.getWidth()));

        // NEXT, add a row for soft caps at the beginning, if needed.
        var t0 = incidents.getFirst().moment();
        if (periods.values().stream()
            .filter(p -> p.start() == t0)
            .anyMatch(p -> p.startCap() == Cap.SOFT)
        ) {
            ++r0;
        }

        // NEXT, add the incidents and periods
        for (var i = 0; i < incidents.size(); i++) {
            var r = r0 + i;
            var incident = incidents.get(i);

            // FIRST, add the incident.  Only include the moment if it differs
            // from the previous incident.
            if (i > 0 && incident.moment() == incidents.get(i - 1).moment()) {
                canvas.puts(0, r, padLeft(incident.label(), labelWidth));
            } else {
                canvas.puts(0, r, getIncidentLabel(incident, labelWidth));
            }

            // NEXT, add the periods.
            for (var j = 0; j < entities.size(); j++) {
                var entity = entities.get(j);
                var period = periods.get(entity.id());
                var concerned = incident.concerns(entity.id());
                var c = c0 + 3*j;

                // Get the incident indices for the period's range.
                var iStart = startIndices.get(period.start());
                var iEnd = endIndices.get(period.end());

                // NEXT, draw the starting soft cap, if any.
                if (i == iStart && period.startCap() == Cap.SOFT) {
                    canvas.puts(c, r - 1, SOFT_START);
                }

                // NEXT, draw the horizontal flag if the entity is concerned.
                if (concerned) {
                    canvas.puts(c - 1, r, H_LINE);
                }

                // NEXT, draw the symbol for the period
                canvas.puts(c, r, getSymbol(period, i, concerned));

                // NEXT, draw the ending soft cap, if any.
                if (i == iEnd && period.endCap() == Cap.SOFT) {
                    canvas.puts(c, r + 1, SOFT_END);
                }
            }
        }

        return canvas.toString();
    }

    public String getSymbol(Period period, int index, boolean concerned) {
        var iStart = (int)startIndices.get(period.start());
        var iEnd = (int)endIndices.get(period.end());

        // FIRST, if this is the only incident for this period, it's a
        // special case.
        if (iStart == iEnd && index == iStart) {
            if (period.startCap() == Cap.HARD && period.endCap() == Cap.HARD) {
                throw new IllegalStateException(
                    "Period with one moment but two hard caps");
            }

            if (period.startCap() == Cap.HARD && period.endCap() == Cap.SOFT) {
                return HARD_START;
            }

            if (period.startCap() == Cap.SOFT && period.endCap() == Cap.HARD) {
                return HARD_END;
            }
        }

        // NEXT, return the normal mark;
        if (index == iStart && period.startCap() == Cap.HARD) {
            return HARD_START;
        } else if (index == iEnd && period.endCap() == Cap.HARD) {
            return HARD_END;
        } else if (concerned) {
            return CONCERNED;
        } else if (iStart <= index && index <= iEnd){
            return V_LINE;
        } else {
            return "";
        }
    }

    private void plotEntities(int c0, int r0) {
        for (var r = 0; r < entities.size(); r++) {
            var c = c0 + r*3;
            canvas.puts(c - 1, r, getEntityLabel(entities.get(r)));
            for (var rLine = r + 1; rLine < r0 - 1; rLine++) {
                canvas.puts(c, rLine, TextCanvas.LIGHT_VERTICAL);
            }
        }
    }

    private String getEntityLabel(Entity entity) {
        return entity.name() + " (" + entity.type() + ")";
    }

    private String getIncidentLabel(Incident incident, int width) {
        // No moment label if we don't have a special format.
        var moment = momentFormatter != null
            ? formatMoment(incident.moment()) + " "
            : "";
        var label = incident.label();

        var minLength = moment.length() + label.length();
        var delta = width - minLength;

        var gap = (delta > 0) ? " ".repeat(delta) : "";

        return moment + gap + label;
    }

    private String formatMoment(int moment) {
        return (momentFormatter != null)
            ? momentFormatter.apply(moment)
            : Integer.toString(moment);
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
