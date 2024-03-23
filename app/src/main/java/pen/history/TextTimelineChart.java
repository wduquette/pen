package pen.history;

import pen.util.TextCanvas;

import java.util.ArrayList;
import java.util.Comparator;
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

    //-------------------------------------------------------------------------
    // Constructor

    public TextTimelineChart(History history) {
        this.history = history;
        this.momentFormatter = history.getMomentFormatter();
    }

    //-------------------------------------------------------------------------
    // Public Methods

    @SuppressWarnings("unused")
    public final Function<Integer, String> getMomentFormatter() {
        return momentFormatter;
    }

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
        // FIRST, get the data
        var entities = new ArrayList<>(history.getEntityMap().values());
        var incidents = history.getIncidents().stream()
            .sorted(Comparator.comparing(Incident::moment))
            .toList();
        var frame = history.getTimeFrame();
        var periods = history.getPeriods(frame);
        assert entities.size() == periods.size();

        // NEXT, get the width of the incident labels.
        var labelWidth = incidents.stream()
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
