package pen.apis;

import pen.history.Cap;
import pen.history.Entity;
import pen.history.HistoryBank;
import pen.history.Incident;
import pen.tcl.Argq;
import pen.tcl.TclEngine;
import pen.tcl.TclExtension;
import tcl.lang.TclException;
import tcl.lang.TclObject;

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

        var ent = tcl.ensemble("entity");
        ent.add("add", this::cmd_entityAdd);

        var inc = tcl.ensemble("incident");

        inc.add("start", this::cmd_incidentStart);
        inc.add("add", this::cmd_incidentAdd);
        inc.add("end", this::cmd_incidentEnd);
    }

    @SuppressWarnings("unused")
    public void reset() {
        bank.clear();
    }

    public HistoryBank getHistory()      { return bank; }

    //-------------------------------------------------------------------------
    // Ensemble: entity *

    // entity add id name type
    private void cmd_entityAdd(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkArgs(argq, 3, 3, "id name type");

        var entity = new Entity(
            argq.next().toString(),
            argq.next().toString(),
            argq.next().toString()
        );

        bank.addEntity(entity);
    }

    //-------------------------------------------------------------------------
    // Ensemble: incident *

    // incident start moment label entityId cap
    private void cmd_incidentStart(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 4, "moment label entityId cap");

        var moment = tcl.toInteger(argq.next());
        var label = argq.next().toString();
        var entityId = argq.next().toString();
        var cap = tcl.toEnum(Cap.class, argq.next());
        var incident = new Incident.EntityStart(moment, label, entityId, cap);

        bank.getIncidents().add(incident);
    }

    // incident add moment label entityId cap
    private void cmd_incidentAdd(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 4, "moment label ?entityId...?");

        var set = new TreeSet<String>();

        var moment = tcl.toInteger(argq.next());
        var label = argq.next().toString();
        while (argq.hasNext()) { set.add(argq.next().toString()); }

        var incident = new Incident.Normal(moment, label, set);

        bank.getIncidents().add(incident);
    }

    // incident end moment label entityId cap
    private void cmd_incidentEnd(TclEngine tcl, Argq argq)
        throws TclException
    {
        tcl.checkMinArgs(argq, 4, "moment label entityId cap");

        var moment = tcl.toInteger(argq.next());
        var label = argq.next().toString();
        var entityId = argq.next().toString();
        var cap = tcl.toEnum(Cap.class, argq.next());
        var incident = new Incident.EntityStart(moment, label, entityId, cap);

        bank.getIncidents().add(incident);
    }
}
