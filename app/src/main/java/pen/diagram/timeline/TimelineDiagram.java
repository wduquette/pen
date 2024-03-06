package pen.diagram.timeline;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import pen.calendars.Calendar;
import pen.calendars.Gregorian;
import pen.stencil.ContentShape;
import pen.stencil.Stencil;

import java.util.*;

@SuppressWarnings("unused")
public class TimelineDiagram extends ContentShape<TimelineDiagram> {
    // TO DO:
    //
    // - Consider defining an "event database" type, that can be
    //   saved to disk and read back.  The diagram would then display the
    //   content.

    //-------------------------------------------------------------------------
    // Instance Variables

    //
    // Configuration
    //

    // The calendar used for date processing; defaults to standard Gregorian.
    private Calendar calendar = Gregorian.CALENDAR;

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

    //-------------------------------------------------------------------------
    // DSL

    public TimelineDiagram calendar(Calendar value) {
        this.calendar = value;
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
        return event.time(time);
    }

    public TimelineEvent event(String dateString) {
        var day = calendar.parse(dateString);
        return event(day);
    }

    //-------------------------------------------------------------------------
    // ContentShape Methods

    @Override
    public Dimension2D getRealSize() {
        return null;
    }

    @Override
    public Bounds draw(Stencil sten) {
        return null;
    }
}
