package pen.tcl;

import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclObject;

/**
 * A wrapper for the JTcl Interp, focusing on the needs of embedding.
 */
public class TclEngine {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final Interp interp = new Interp();

    //-------------------------------------------------------------------------
    // Constructor

    public TclEngine() {
        // Nothing to do yet
    }

    //-------------------------------------------------------------------------
    // Interpreter API

    /**
     * Gets the underlying Interp as an escape hatch.
     * @return The Interp
     */
    public Interp interp() {
        return interp;
    }

    /**
     * Evaluates the script as a Tcl script, returning the result.
     * @param script The script.
     * @return The result
     * @throws TclException on error.
     */
    public TclObject eval(String script) throws TclException {
        interp.eval(script);
        return interp.getResult();
    }

    //-------------------------------------------------------------------------
    // Command Definition API

    /**
     * Adds a command to the interp given its name and a function that
     * implements the command.
     * @param name The name
     * @param proc The function
     */
    public void add(String name, TclEngineProc proc) {
        var cmd = new TclEngineCommand(this, 1, proc);
        interp.createCommand(name, cmd);
    }

    //-------------------------------------------------------------------------
    // Helpers: Exceptions

    public TclException error(String message) {
        return new TclEngineException(this, message);
    }

    public TclException error(String message, Throwable cause) {
        return new TclEngineException(this, message, cause);
    }



}
