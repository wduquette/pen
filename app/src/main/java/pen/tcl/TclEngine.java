package pen.tcl;

import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclList;
import tcl.lang.TclObject;

import java.io.File;

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

    /**
     * Evaluates the file's content as a Tcl script.
     * @param file The file
     * @throws TclException on Tcl error
     */
    public void evalFile(File file) throws TclException {
        interp.evalFile(file.toString());
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

    /**
     * Adds an ensemble to the engine given its name.
     * @param name The name
     */
    public TclEnsemble ensemble(String name) {
        var ensemble = new TclEnsemble(this, 1);
        interp.createCommand(name, ensemble);
        return ensemble;
    }

    //-------------------------------------------------------------------------
    // Helpers: Argument Processing

    public void checkArgs(ArgQ argq, int argMin, int argMax, String argSig)
        throws TclException
    {
        if (argq.size() < argMin || argq.size() > argMax) {
            var prefix = TclList.newInstance();
            for (int i = 0; i < argq.getPrefixTokens(); i++) {
                TclList.append(interp, prefix, argq.asCommandArray()[i]);
            }
            throw error("wrong # args: should be \"" + prefix + " " +
                argSig + "\"");
        }
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
