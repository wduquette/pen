package pen.apis;

import pen.history.Cap;
import pen.history.Entity;
import pen.history.HistoryBank;
import pen.history.Incident;
import pen.tcl.Argq;
import pen.tcl.TclEngine;
import pen.tcl.TclExtension;
import tcl.lang.TclException;

import java.util.*;

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

        // history entity id name type
        // history enters id moment label cap
        // history event moment label entityIds
        // history exits id moment label cap
        // history calendar calendarFile -format formatString

        var hist = tcl.ensemble("history");
        hist.add("entity", this::cmd_historyEntity);
        hist.add("begins", this::cmd_historyBegins);
        hist.add("ends",   this::cmd_historyEnds);
        hist.add("enters", this::cmd_historyEnters);
        hist.add("exits",  this::cmd_historyExits);
        hist.add("event",  this::cmd_historyEvent);
    }

    @SuppressWarnings("unused")
    public void reset() {
        bank.clear();
    }

    public HistoryBank getHistory()      { return bank; }

    //-------------------------------------------------------------------------
    // Ensemble: history *

    // history entity id name type
    //
    // Adds a new entity to the history, assigning it a unique ID, a
    // name for display, and a type.
    private void cmd_historyEntity(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 3, 3, "id name type");

        var entity = new Entity(
            argq.next().toString().trim(),
            argq.next().toString().trim(),
            argq.next().toString().trim()
        );

        if (entity.id().isEmpty()) {
            throw tcl.expected("entity ID", entity.id());
        }

        // TODO: Need tcl.toIdentifier()!
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

        var moment = tcl.toInteger(argq.next());

        var id = argq.next().toString().trim();

        var entity = bank.getEntity(id).orElseThrow(() ->
            tcl.expected("known entity ID", id));

        var label = argq.hasNext()
            ? argq.next().toString().trim()
            : entity.name() + " begins";

        var incident = new Incident.Beginning(moment, label, id, Cap.HARD);

        // TODO: Add integrity checks.
        bank.getIncidents().add(incident);
    }

    // history ends id moment ?label?
    private void cmd_historyEnds(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 2, 3, "moment id ?label?");

        var moment = tcl.toInteger(argq.next());

        var id = argq.next().toString().trim();

        var entity = bank.getEntity(id).orElseThrow(() ->
            tcl.expected("known entity ID", id));

        var label = argq.hasNext()
            ? argq.next().toString().trim()
            : entity.name() + " ends";

        var incident = new Incident.Ending(moment, label, id, Cap.HARD);

        // TODO: Add integrity checks.
        bank.getIncidents().add(incident);
    }

    // history enters id moment ?label?
    private void cmd_historyEnters(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 2, 3, "moment id ?label?");

        var moment = tcl.toInteger(argq.next());

        var id = argq.next().toString().trim();

        var entity = bank.getEntity(id).orElseThrow(() ->
            tcl.expected("known entity ID", id));

        var label = argq.hasNext()
            ? argq.next().toString().trim()
            : entity.name() + " enters";

        var incident = new Incident.Beginning(moment, label, id, Cap.SOFT);

        // TODO: Add integrity checks.
        bank.getIncidents().add(incident);
    }

    // history exits id moment ?label?
    private void cmd_historyExits(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 2, 3, "moment id ?label?");

        var moment = tcl.toInteger(argq.next());

        var id = argq.next().toString().trim();

        var entity = bank.getEntity(id).orElseThrow(() ->
            tcl.expected("known entity ID", id));

        var label = argq.hasNext()
            ? argq.next().toString().trim()
            : entity.name() + " exits";

        var incident = new Incident.Ending(moment, label, id, Cap.SOFT);

        // TODO: Add integrity checks.
        bank.getIncidents().add(incident);
    }

    // history event moment label ?entity...?
    private void cmd_historyEvent(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 4, "moment label ?entityId...?");

        var set = new TreeSet<String>();

        var moment = tcl.toInteger(argq.next());
        var label = argq.next().toString().trim();
        while (argq.hasNext()) {
            var id = argq.next().toString().trim();

            if (bank.getEntity(id).isEmpty()) {
                throw tcl.expected("known entity ID", id);
            }
            set.add(argq.next().toString());
        }

        var incident = new Incident.Normal(moment, label, set);

        // TODO: Check integrity across events concerning this entity.
        // TODO: Add history::entityExits(...), which does the necessary checks.
        // TODO: Do not expose getIncidents as modifiable.

        bank.getIncidents().add(incident);
    }
}
