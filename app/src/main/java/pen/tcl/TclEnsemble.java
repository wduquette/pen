package pen.tcl;

import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclObject;

import java.util.TreeMap;

public class TclEnsemble implements Command {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The engine to which the command belongs
    private final TclEngine engine;

    // The number of command prefix tokens
    private final int prefixTokens;

    private final TreeMap<String,Command> subcommands = new TreeMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Constructs the ensemble.  Intentionally package private; a
     * TclEnsemble is always created by a TclEngine or TclEnsemble.
     * @param engine The engine (needed?)
     * @param prefixTokens The number of command prefix tokens
     */
    TclEnsemble(TclEngine engine, int prefixTokens) {
        this.engine = engine;
        this.prefixTokens = prefixTokens;
    }

    //-------------------------------------------------------------------------
    // Execution

    @Override
    public void cmdProc(Interp interp, TclObject[] tclObjects) throws TclException {
        if (tclObjects.length <= prefixTokens) {
            // TODO: better error message
            throw engine.error("Missing subcommand");
        }
        var name = tclObjects[prefixTokens].toString();
        var cmd = subcommands.get(name);

        if (cmd == null) {
            // TODO Better error message
            throw engine.error("Unknown subcommand: " + name);
        }

        cmd.cmdProc(interp, tclObjects);
    }

    //-------------------------------------------------------------------------
    // Command and Ensemble Definition

    /**
     * Adds a command to the ensemble given its name and a function that
     * implements the command.
     * @param name The name
     * @param proc The function
     */
    public void add(String name, TclEngineProc proc) {
        var cmd = new TclEngineCommand(engine, prefixTokens + 1, proc);
        subcommands.put(name, cmd);
    }

    /**
     * Adds a sub-ensemble to the ensemble given its name and a function that
     * implements the command.
     * @param name The name
     */
    public TclEnsemble ensemble(String name) {
        var ensemble = new TclEnsemble(engine, prefixTokens + 1);
        subcommands.put(name, ensemble);
        return ensemble;
    }



}
