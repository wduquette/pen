package pen.history;

import pen.calendars.Calendar;
import pen.calendars.formatter.DateFormat;

import java.util.*;
import java.util.stream.Stream;

public class HistoryQuery implements History {
    //-------------------------------------------------------------------------
    // Instance Variables

    private Calendar calendar;
    private DateFormat dateFormat;
    private Set<Incident> incidents;
    private Set<Entity> entities;
    private Stream<Incident> incidentStream;
    private Stream<Entity> entityStream;

    //-------------------------------------------------------------------------
    // Constructor

    public HistoryQuery(History history) {
        this.calendar = history.getCalendar().orElse(null);
        this.dateFormat = history.getDateFormat();
        this.incidents = new HashSet<>(history.getIncidents());
        this.entities = new HashSet<>(history.getEntityMap().values());
        this.incidentStream = incidents.stream();
        this.entityStream = entities.stream();
    }

    //-------------------------------------------------------------------------
    // Query API

    // TODO: start(dateString), end(dateString), concerns(entityIds)

    public HistoryQuery start(int moment) {
        incidentStream = incidentStream
            .filter(incident -> incident.moment() >= moment);
        return this;
    }

    public HistoryQuery end(int moment) {
        incidentStream = incidentStream
            .filter(incident -> incident.moment() <= moment);
        return this;
    }

    public HistoryQuery concerns(String entityId) {
        incidentStream = incidentStream
            .filter(incident -> incident.concerns(entityId));
        return this;
    }

    //-------------------------------------------------------------------------
    // History API

    @Override
    public Optional<Calendar> getCalendar() {
        return Optional.ofNullable(calendar);
    }

    @Override
    public DateFormat getDateFormat() {
        return dateFormat;
    }

    @Override
    public Map<String, Entity> getEntityMap() {
        return null;
    }

    @Override
    public List<Incident> getIncidents() {
        return incidentStream
            .sorted(Comparator.comparing(Incident::moment))
            .toList();
    }

    @Override
    public String toTimelineChart() {
        return null;
    }
}
