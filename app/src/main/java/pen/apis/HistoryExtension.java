package pen.apis;

import pen.DataFileException;
import pen.DataFiles;
import pen.calendars.Calendar;
import pen.calendars.CalendarException;
import pen.calendars.formatter.DateFormat;
import pen.history.Cap;
import pen.history.Entity;
import pen.history.HistoryBank;
import pen.history.Incident;
import pen.tcl.Argq;
import pen.tcl.TclEngine;
import pen.tcl.TclExtension;
import tcl.lang.TclException;
import tcl.lang.TclObject;

import java.io.File;
import java.util.Map;
import java.util.TreeSet;

/**
 * A TclEngine extension for defining histories.
 */
public class HistoryExtension implements TclExtension {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The TclEngine in use.  Set by initialize().
    private TclEngine tcl;

    // Data stores
    private final HistoryBank bank = new HistoryBank();

    private Calendar calendar;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a new TclEngine extension for defining a history
     */
    public HistoryExtension() {
        reset();
    }

    public void initialize(TclEngine tcl) {
        this.tcl = tcl;

        var hist = tcl.ensemble("history");
        hist.add("calendar", this::cmd_historyCalendar);
        hist.add("entity",   this::cmd_historyEntity);
        hist.add("begins",   this::cmd_historyBegins);
        hist.add("ends",     this::cmd_historyEnds);
        hist.add("enters",   this::cmd_historyEnters);
        hist.add("exits",    this::cmd_historyExits);
        hist.add("event",    this::cmd_historyEvent);
    }

    @SuppressWarnings("unused")
    public void reset() {
        bank.clear();
    }

    public HistoryBank getHistory()      { return bank; }

    //-------------------------------------------------------------------------
    // Ensemble: history *

    // history calendar calendarFile name ?outputFormat?
    //
    // Adds a calendar for processing input dates and formatting output dates.
    private void cmd_historyCalendar(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 2, 3, "calendarFile name ?outputFormat?");

        var calendarFile = argq.next().toString();
        var calendarPath = tcl.getWorkingDirectory()
            .resolve(new File(calendarFile).toPath());
        Map<String, Calendar> map;

        try {
            map = DataFiles.loadCalendar(calendarPath);
        } catch (DataFileException ex) {
            throw tcl.error("Could not load calendar file " + calendarFile, ex);
        }

        var name = argq.next().toString();
        if (!map.containsKey(name)) {
            throw tcl.expected("calendar name", name);
        }

        calendar = map.get(name);
        bank.setMomentFormatter(m -> calendar.format(m));

        if (argq.hasNext()) {
            var dateFormat = toDateFormat(argq.next());
            bank.setMomentFormatter(m -> calendar.format(dateFormat, m));
        }
    }


    // history entity id name type
    //
    // Adds a new entity to the history, assigning it a unique ID, a
    // name for display, and a type.
    private void cmd_historyEntity(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 3, 3, "id name type");

        var entity = new Entity(
            tcl.toIdentifier(argq.next()),
            argq.next().toString().trim(),
            tcl.toIdentifier(argq.next())
        );

        if (bank.getEntity(entity.id()).isPresent()) {
            throw tcl.error("Duplicate entity: \"" + entity.id());
        }

        if (entity.name().isEmpty()) {
            throw tcl.expected("entity name", entity.name());
        }

        if (entity.type().isEmpty()) {
            throw tcl.expected("entity type", entity.type());
        }

        bank.addEntity(entity);
    }

    // history begins id moment label
    private void cmd_historyBegins(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 2, 3, "moment id ?label?");

        var moment = toMoment(argq.next());
        var entity = toEntity(argq.next());
        var label = argq.hasNext()
            ? argq.next().toString().trim()
            : entity.name() + " begins";

        var incident = new Incident.Beginning(
            moment, label, entity.id(), Cap.HARD);

        // TODO: Add integrity checks.
        bank.getIncidents().add(incident);
    }

    // history ends id moment ?label?
    private void cmd_historyEnds(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 2, 3, "moment id ?label?");

        var moment = toMoment(argq.next());
        var entity = toEntity(argq.next());
        var label = argq.hasNext()
            ? argq.next().toString().trim()
            : entity.name() + " ends";

        var incident = new Incident.Ending(
            moment, label, entity.id(), Cap.HARD);

        // TODO: Add integrity checks.
        bank.getIncidents().add(incident);
    }

    // history enters id moment ?label?
    private void cmd_historyEnters(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 2, 3, "moment id ?label?");

        var moment = toMoment(argq.next());
        var entity = toEntity(argq.next());
        var label = argq.hasNext()
            ? argq.next().toString().trim()
            : entity.name() + " enters";

        var incident = new Incident.Beginning(
            moment, label, entity.id(), Cap.SOFT);

        // TODO: Add integrity checks.
        bank.getIncidents().add(incident);
    }

    // history exits id moment ?label?
    private void cmd_historyExits(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 2, 3, "moment id ?label?");

        var moment = toMoment(argq.next());
        var entity = toEntity(argq.next());
        var label = argq.hasNext()
            ? argq.next().toString().trim()
            : entity.name() + " exits";

        var incident = new Incident.Ending(
            moment, label, entity.id(), Cap.SOFT);

        // TODO: Add integrity checks.
        bank.getIncidents().add(incident);
    }

    // history event moment label ?entity...?
    private void cmd_historyEvent(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 4, "moment label ?entityId...?");

        var set = new TreeSet<String>();

        var moment = toMoment(argq.next());
        var label = argq.next().toString().trim();
        while (argq.hasNext()) {
            var id = argq.next().toString().trim();

            if (bank.getEntity(id).isEmpty()) {
                throw tcl.expected("known entity ID", id);
            }
            set.add(argq.next().toString());
        }

        // TODO: Check integrity of events.
        var incident = new Incident.Normal(moment, label, set);
        bank.getIncidents().add(incident);
    }

    //-------------------------------------------------------------------------
    // Conversion Helpers

    // Converts a DateFormat string to a DateFormat.
    private DateFormat toDateFormat(TclObject arg) throws TclException {
        try {
            return new DateFormat(arg.toString());
        } catch (CalendarException ex) {
            throw tcl.expected("date format", arg);
        }
    }

    // Converts an entity ID into an entity.
    private Entity toEntity(TclObject arg) throws TclException {
        return bank.getEntity(arg.toString())
            .orElseThrow(() -> tcl.expected("known entity ID", arg));
    }

    // Converts an argument to a moment integer.  If there's a calendar,
    // we assume we've got a date to be parsed; otherwise, we assume we
    // have an integer moment.
    private int toMoment(TclObject arg) throws TclException {
        if (calendar == null) {
            return tcl.toInteger(arg);
        }

        try {
            return calendar.parse(arg.toString());
        } catch (CalendarException ex) {
            throw tcl.expected("calendar date", arg);
        }
    }
}
