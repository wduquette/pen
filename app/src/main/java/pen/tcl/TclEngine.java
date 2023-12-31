package pen.tcl;

import tcl.lang.*;

import java.io.File;
import java.util.List;

/**
 * A wrapper for the JTcl Interp, focusing on the needs of embedding.
 */
@SuppressWarnings("unused")
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
     * @throws TclEngineException on error.
     */
    public TclObject eval(String script) throws TclEngineException {
        try {
            interp.eval(script);
            return interp.getResult();
        } catch (TclException ex) {
            throw new TclEngineException(this, ex);
        }
    }

    /**
     * Evaluates the file's content as a Tcl script.
     * @param file The file
     * @throws TclEngineException on Tcl error
     */
    public void evalFile(File file) throws TclEngineException {
        try {
            interp.evalFile(file.toString());
        } catch (TclException ex) {
            throw new TclEngineException(this, ex);
        }
    }

    /**
     * Gets the script line number of the just thrown TclException.
     * @return the line number
     */
    public int getErrorLine() {
        return interp.getErrorLine();
    }

    /**
     * Gets the errorInfo of the just thrown TclException, or "" if none.
     * @return The errorInfo
     */
    public String getErrorInfo() {
        try {
            return interp.getVar("errorInfo", TCL.GLOBAL_ONLY).toString();
        } catch (Exception ex) {
            return "";
        }
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

    public double toDouble(TclObject arg) throws TclException {
        return TclDouble.get(interp, arg);
    }

    public <E extends Enum<E>> E toEnum(Class<E> cls, TclObject arg)
        throws TclException
    {
        try {
            return Enum.valueOf(cls, arg.toString().toUpperCase());
        } catch (Exception ex) {
            throw expected(cls.getSimpleName(), arg);
        }
    }

    //-------------------------------------------------------------------------
    // Helpers: Results

    public void setResult(TclObject newResult) {
        interp.setResult(newResult);
    }

    public void setResult(boolean value) {
        interp.setResult(value);
    }

    public void setResult(double value) {
        interp.setResult(value);
    }

    public void setResult(long value) {
        interp.setResult(value);
    }

    public void setResult(String value) {
        interp.setResult(value);
    }

    public void setResult(Enum<?> symbol) {
        interp.setResult(symbol.toString().toLowerCase());
    }

    public void setResult(List<String> list) {
        interp.setResult(list2tclList(list));
    }

    //-------------------------------------------------------------------------
    // TclList Builder

    public TclListBuilder list() {
        return new TclListBuilder();
    }

    public class TclListBuilder {
        private final TclObject result = TclList.newInstance();

        public TclObject get() {
            return result;
        }

        public TclListBuilder item(TclObject value)
            throws TclException
        {
            TclList.append(interp, result, value);
            return this;
        }

        public TclListBuilder item(boolean value)
            throws TclException
        {
            TclList.append(interp, result, TclBoolean.newInstance(value));
            return this;
        }

        public TclListBuilder item(double value)
            throws TclException
        {
            TclList.append(interp, result, TclDouble.newInstance(value));
            return this;
        }

        public TclListBuilder item(long value)
            throws TclException
        {
            TclList.append(interp, result, TclInteger.newInstance(value));
            return this;
        }

        public TclListBuilder item(String value)
            throws TclException
        {
            TclList.append(interp, result, TclString.newInstance(value));
            return this;
        }

        public TclListBuilder item(Enum<?> symbol)
            throws TclException
        {
            TclList.append(interp, result,
                TclString.newInstance(symbol.toString().toLowerCase()));
            return this;
        }

        public TclListBuilder item(List<String> list)
            throws TclException
        {
            TclList.append(interp, result, list2tclList(list));
            return this;
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

    public TclException expected(String name, Object value) {
        return new TclEngineException(this,
            "Expected " + name + ", got \"" + value + "\"");
    }

    //-------------------------------------------------------------------------
    // Helpers: Type Conversions

    public TclObject list2tclList(List<String> list) {
        var result = TclList.newInstance();

        try {
            for (var item : list) {
                TclList.append(interp, result, TclString.newInstance(item));
            }
        } catch (TclException ex) {
            throw new IllegalStateException("Unexpected failure", ex);
        }

        return result;
    }

}
