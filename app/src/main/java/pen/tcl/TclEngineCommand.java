package pen.tcl;

import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclObject;

/**
 * A JTcl Command that calls a TclEngineProc lambda or method reference.
 */
public class TclEngineCommand implements Command {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The engine to which the command belongs
    private final TclEngine engine;

    // The number of command prefix tokens
    private final int prefixTokens;

    // The actual method reference or lambda that implements the command.
    private final TclEngineProc proc;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Constructs the command.  Intentionally package private; a
     * TclEngineCommand is always created by a TclEngine or TclEnsemble.
     * @param engine The engine (needed?)
     * @param prefixTokens The number of command prefix tokens
     * @param proc The function to call
     */
    TclEngineCommand(TclEngine engine, int prefixTokens, TclEngineProc proc) {
        this.engine = engine;
        this.prefixTokens = prefixTokens;
        this.proc = proc;
    }

    //-------------------------------------------------------------------------
    // Command API

    @Override
    public void cmdProc(Interp interp, TclObject[] args) throws TclException {
        try {
            proc.run(engine, new Argq(args, prefixTokens));
        } catch (TclException ex) {
            throw ex;
        } catch (Exception ex) {
            throw engine.error(
                "Unexpected Java exception: " + ex.getMessage(), ex);
        }
    }
}
