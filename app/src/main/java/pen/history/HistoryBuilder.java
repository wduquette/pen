package pen.history;

import pen.calendars.BasicCalendar;
import pen.calendars.Calendar;
import pen.calendars.formatter.DateFormat;

import java.util.*;

/**
 * Provides a DSL for building {@link HistoryBank} objects.
 */
@SuppressWarnings("unused")
public class HistoryBuilder {
    //-------------------------------------------------------------------------
    // Instance Variables

    //
    // Configuration
    //

    // The calendar used for date processing, if any.
    private Calendar calendar = null;

    // The date format, used when a calendar is defined.
    private DateFormat dateFormat = BasicCalendar.ERA_YMD;

    // The History being build.
    private final HistoryBank history = new HistoryBank();

    //-------------------------------------------------------------------------
    // Constructor

    public HistoryBuilder() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // Getters

    public Calendar getCalendar() {
        return calendar;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public HistoryBank getHistory() {
        return history;
    }

    //-------------------------------------------------------------------------
    // DSL

    public HistoryBuilder calendar(Calendar value) {
        this.calendar = value;
        return this;
    }

    public HistoryBuilder dateFormat(DateFormat format) {
        this.dateFormat = format;
        return this;
    }

    public HistoryBuilder dateFormat(String format) {
        this.dateFormat = new DateFormat(format);
        return this;
    }

    public HistoryBuilder entity(String id, String name, String type) {
        var entity = new Entity(id, name, type);
        history.addEntity(entity);
        return this;
    }

    public HistoryBuilder start(int moment, String label, String entityId, Cap cap) {
        history.getIncidents().add(
            new Incident.EntityStart(moment, label, entityId, cap)
        );
        return this;
    }

    public HistoryBuilder start(String dateString, String label, String entityId, Cap cap) {
        if (calendar == null) {
            throw new IllegalArgumentException("No calendar specified.");
        }
        var day = calendar.parse(dateFormat, dateString);

        history.getIncidents().add(
            new Incident.EntityStart(day, label, entityId, cap)
        );
        return this;
    }

    public HistoryBuilder end(int moment, String label, String entityId, Cap cap) {
        history.getIncidents().add(
            new Incident.EntityEnd(moment, label, entityId, cap)
        );
        return this;
    }

    public HistoryBuilder end(String dateString, String label, String entityId, Cap cap) {
        if (calendar == null) {
            throw new IllegalArgumentException("No calendar specified.");
        }
        var day = calendar.parse(dateFormat, dateString);

        history.getIncidents().add(
            new Incident.EntityEnd(day, label, entityId, cap)
        );
        return this;
    }

    public HistoryBuilder normal(int moment, String label, String... entityIds) {
        history.getIncidents().add(
            new Incident.Normal(moment, label, Set.of(entityIds))
        );
        return this;
    }

    public HistoryBuilder normal(String dateString, String label, String... entityIds) {
        if (calendar == null) {
            throw new IllegalArgumentException("No calendar specified.");
        }
        var day = calendar.parse(dateFormat, dateString);
        history.getIncidents().add(
            new Incident.Normal(day, label, Set.of(entityIds))
        );
        return this;
    }
}

