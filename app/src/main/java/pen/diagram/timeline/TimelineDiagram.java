package pen.diagram.timeline;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;
import pen.calendars.BasicCalendar;
import pen.calendars.Calendar;
import pen.calendars.Gregorian;
import pen.calendars.formatter.DateFormat;
import pen.stencil.*;

import java.util.*;

import static pen.stencil.Stencil.rectangle;
import static pen.stencil.Stencil.text;

@SuppressWarnings("unused")
public class TimelineDiagram extends ContentShape<TimelineDiagram> {
    public static final PenFont NORMAL_FONT = PenFont.SANS12;
    public static final PenFont TITLE_FONT = new PenFont.Builder("title")
        .family("sans-serif").size(18).build();

    // TO DO:
    //
    // - Consider defining an "event database" type, that can be
    //   saved to disk and read back.  The diagram would then display the
    //   content.
    // - Allow grouping entities by type
    // - Add style parameters to the diagram class

    //-------------------------------------------------------------------------
    // Instance Variables

    //
    // Configuration
    //

    // The calendar used for date processing; defaults to standard Gregorian.
    private Calendar calendar = Gregorian.CALENDAR;

    // The title
    private String title = "Timeline";

    // The date format, for events.
    private DateFormat dateFormat = BasicCalendar.ERA_YMD;

    //
    // Timeline Data
    //

    // The entities in this diagram.  Use a LinkedHashMap to preserve
    // creation order.
    // TODO: We want a way to group these.
    private final SequencedMap<String,TimelineEntity> entityMap =
        new LinkedHashMap<>();

    // The events in this diagram
    private final List<TimelineEvent> events = new ArrayList<>();

    //-------------------------------------------------------------------------
    // Constructor

    public TimelineDiagram() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // Getters

    public Calendar getCalendar() {
        return calendar;
    }

    public String getTitle() {
        return title;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    //-------------------------------------------------------------------------
    // DSL

    public TimelineDiagram calendar(Calendar value) {
        this.calendar = value;
        return this;
    }

    public TimelineDiagram title(String title) {
        this.title = title;
        return this;
    }

    public  TimelineDiagram dateFormat(DateFormat format) {
        this.dateFormat = format;
        return this;
    }

    public  TimelineDiagram dateFormat(String format) {
        this.dateFormat = new DateFormat(format);
        return this;
    }

    public TimelineEntity entity(String id) {
        var entity = new TimelineEntity(id);
        entityMap.put(entity.getId(), entity);
        return entity;
    }

    public TimelineEvent event(int time) {
        var event = new TimelineEvent();
        events.add(event);
        return event.day(time);
    }

    public TimelineEvent event(String dateString) {
        var day = calendar.parse(dateString);
        return event(day);
    }

    //-------------------------------------------------------------------------
    // ContentShape Methods

    @Override
    public Dimension2D getRealSize() {
        var layout = new Layout();
        return layout.getRealSize();
    }

    @Override
    public Bounds draw(Stencil sten) {
        if (events.isEmpty() || entityMap.isEmpty()) {
            return new BoundingBox(0, 0, 1, 1);
        }

        // FIRST, get dimensions
        var layout = new Layout();
        var bounds = getRealBounds();
        var yBody = bounds.getMinY() + Pen.getTextHeight(TITLE_FONT, title) +
            layout.titlePad;
        var xBody = bounds.getMinX() + layout.eventTextSize.getWidth() +
            layout.eventPad;

        sten.draw(rectangle()
            .at(bounds.getMinX(), bounds.getMinY())
            .size(bounds.getWidth(), bounds.getHeight())
            .foreground(Color.PURPLE)
        );

        // NEXT, draw the title
        sten.draw(text()
            .text(title)
            .font(TITLE_FONT)
            .at(bounds.getCenterX(), bounds.getMinY())
            .tack(Tack.NORTH)
        );

        // NEXT, draw the events
        var xEvent = xBody - layout.eventPad;
        var yEvent = yBody + layout.eventSpacing;

        // TODO: Sort, if not in order.
        for (var event : events) {
            var text = getEventText(event);
            var textSize = Pen.getTextSize(NORMAL_FONT, text);
            sten.draw(text()
                .text(text)
                .font(NORMAL_FONT)
                .at(xEvent, yEvent)
                .tack(Tack.NORTHEAST)
            );
            yEvent += textSize.getHeight() + layout.eventSpacing;
        }

        // NEXT, draw the entities
        var xEntity = xBody;
        for (var entity : entityMap.values()) {

        }

        return getBounds();
    }

    private String getEventText(TimelineEvent event) {
        var date = calendar.format(dateFormat, event.getDay());
        return event.getText() + " " + date;
    }

    //-------------------------------------------------------------------------
    // Layout

    private class Layout {
        //---------------------------------------------------------------------
        // Instance Variables

        private double titlePad = 5;
        private double eventSpacing = 4;
        private double eventPad = 5;
        private double entityPad = 1;
        private double entitySpacing = 5;
        private Dimension2D eventTextSize;
        private Dimension2D bodySize;
        private Dimension2D realSize = new Dimension2D(0,0);


        //---------------------------------------------------------------------
        // Constructor

        // - Sort events by time.
        // - Compute time-to-Y-coordinate list
        //   - Include the event times
        //   - Include entity start,stop times if not already represented.
        //   - Entity start/stop prior to first event or after last event
        //     are treated as Limit.External.
        // - For each time, include the top and bottom Y coordinate.
        //   - Top is the top of the matching event text.
        //   - Bottom is the bottom of the matching event text.
        //   - Use default pixel gap for unmatched entity start/stop times.
        // - When I actually implement fuzzy start and stop
        //   - Fuzziness is orthogonal to the start or stop time.

        public Layout() {
            // FIRST, if there's not enough data to draw anything, we don't
            // have layout.
            if (events.isEmpty() || entityMap.isEmpty()) {
                return;
            }

            // NEXT, compute the sizes.
            this.eventTextSize = computeEventTextSize();
            this.bodySize = computeBodySize();
            this.realSize = computeRealSize();
        }

        private Dimension2D computeEventTextSize() {
            var maxWidth = 0.0;
            var height = 0.0;

            for (var event : events) {
                var dim = Pen.getTextSize(NORMAL_FONT, getEventText(event));
                maxWidth = Math.max(dim.getWidth(), maxWidth);
                height = height + dim.getHeight();
            }

            return new Dimension2D(
                maxWidth,
                height + events.size() * eventSpacing
            );
        }

        private Dimension2D computeBodySize() {
            var height = eventTextSize.getHeight();
            var width = 0.0;

            for (var entity : entityMap.values()) {
                var textWidth = Pen.getTextHeight(NORMAL_FONT, entity.getName());
                width += entitySpacing + 2*entityPad + textWidth;
            }

            return new Dimension2D(width, height);
        }

        private Dimension2D computeRealSize() {
            var width = eventTextSize.getWidth() + bodySize.getWidth();
            var height = Pen.getTextHeight(TITLE_FONT, title) + titlePad
                + eventTextSize.getHeight();
            return new Dimension2D(width, height);
        }

        //---------------------------------------------------------------------
        // Accessors

        public Dimension2D getRealSize() {
            return realSize;
        }
    }

    private record EntityDim(double top, double bottom) {}
}

