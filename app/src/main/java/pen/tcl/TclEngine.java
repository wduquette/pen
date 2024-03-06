package pen.tcl;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import tcl.lang.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A wrapper for the JTcl Interp, focusing on the needs of embedding.
 */
@SuppressWarnings("unused")
public class TclEngine {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final Interp interp = new Interp();
    private final List<TclExtension> extensions = new ArrayList<>();

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
     * Evaluates the object as a Tcl script, returning the result.
     * @param script The script.
     * @return The result
     * @throws TclEngineException on error.
     */
    public TclObject eval(TclObject script) throws TclEngineException {
        try {
            interp.eval(script, TCL.EVAL_GLOBAL);
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

    /**
     * Installs the TclExtension, making its commands available.
     * @param extension The extension
     */
    public void install(TclExtension extension) {
        extension.initialize(this);
        extensions.add(extension);
    }

    public void resetExtensions() {
        extensions.forEach(TclExtension::reset);
    }

    //-------------------------------------------------------------------------
    // Helpers: Argument Processing

    public void checkArgs(Argq argq, int argMin, int argMax, String argSig)
        throws TclException
    {
        if (argq.size() < argMin || argq.size() > argMax) {
            throw wrongNumArgs(argq, argSig);
        }
    }

    public void checkMinArgs(Argq argq, int argMin, String argSig)
        throws TclException
    {
        if (argq.size() < argMin) {
            throw wrongNumArgs(argq, argSig);
        }
    }

    /**
     * Given a Tcl List, returns the list as an Argq with no prefix tokens.
     * @param arg The Tcl list
     * @return The Argq
     * @throws TclException on list error
     */
    public Argq toArgq(TclObject arg) throws TclException {
        return new Argq(TclList.getElements(interp, arg), 0);
    }

    public Argq toArgq(String opt, Argq argq) throws TclException {
        return toArgq(toOptArg(opt, argq));
    }

    public TclObject toOptArg(String opt, Argq argq) throws TclException {
        if (argq.hasNext()) {
            return argq.next();
        } else if (opt.startsWith("-")) {
            throw error("missing value for option " + opt);
        } else {
            throw expected("option",  opt);
        }
    }

    public String toString(String opt, Argq argq) throws TclException {
        return toOptArg(opt, argq).toString();
    }

    public boolean toBoolean(TclObject arg) throws TclException {
        return TclBoolean.get(interp, arg);
    }

    public boolean toBoolean(String opt, Argq argq) throws TclException {
        return TclBoolean.get(interp, toOptArg(opt, argq));
    }

    public Color toColor(TclObject arg) throws TclException {
        try {
            return Color.valueOf(arg.toString());
        } catch (Exception ex) {
            throw expected("color", arg);
        }
    }

    public Color toColor(String opt, Argq argq) throws TclException {
        return toColor(toOptArg(opt, argq));
    }

    public double toDouble(TclObject arg) throws TclException {
        return TclDouble.get(interp, arg);
    }

    public double toDouble(String opt, Argq argq) throws TclException {
        return TclDouble.get(interp, toOptArg(opt, argq));
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

    public <E extends Enum<E>> E toEnum(Class<E> cls, String opt, Argq argq)
        throws TclException
    {
        return toEnum(cls, toOptArg(opt, argq));
    }

    public int toInteger(TclObject arg) throws TclException {
        return TclInteger.getInt(interp, arg);
    }

    public int toInteger(String opt, Argq argq) throws TclException {
        return TclInteger.getInt(interp, toOptArg(opt, argq));
    }

    /**
     * Converts a string argument value to a map entry value
     * @param what What we are looking for, for error messages.
     * @param map The map
     * @param arg The argument
     * @return The entry's value
     * @param <V> The value type
     * @throws TclException If the value is not found.
     */
    public <V> V toMapEntry(String what, Map<String,V> map, TclObject arg)
        throws TclException
    {
        var key = arg.toString();

        var value = map.get(key);
        if (value == null) {
            throw expected(what, key);
        }

        return value;
    }

    /**
     * Converts a string option argument to a map entry value
     * @param what What we are looking for, for error messages.
     * @param map The map
     * @param opt The option
     * @param argq The argument queue
     * @return The entry's value
     * @param <V> The value type
     * @throws TclException If the value is not found.
     */
    public <V> V toMapEntry(
        String what,
        Map<String,V> map,
        String opt,
        Argq argq
    ) throws TclException {
        return toMapEntry(what, map, toOptArg(opt, argq));
    }

    /**
     * Converts an argument containing a string "num,num" into a Point2D
     * @param arg The argument
     * @return The point
     * @throws TclException on parse error
     */
    public Point2D toPoint(TclObject arg) throws TclException {
        var tokens = arg.toString().split(",");
        double x;
        double y;

        // TODO: Fix up error messages to be more helpful and Tcl-like
        if (tokens.length == 2) {
            try {
                x = Double.parseDouble(tokens[0]);
            } catch (Exception ex) {
                throw expected("x,y point", arg);
            }

            try {
                y = Double.parseDouble(tokens[1]);
            } catch (Exception ex) {
                throw expected("x,y point", arg);
            }

            return new Point2D(x, y);
        } else {
            throw expected("x,y point", arg);
        }
    }

    public Point2D toPoint(String opt, Argq argq) throws TclException {
        return toPoint(toOptArg(opt, argq));
    }

    public List<Point2D> toPointList(TclObject arg) throws TclException {
        var q = toArgq(arg);
        var list = new ArrayList<Point2D>();

        while (q.hasNext()) {
            list.add(toPoint(q.next()));
        }

        return list;
    }

    public List<Point2D> toPointList(String opt, Argq argq)
        throws TclException
    {
        return toPointList(toOptArg(opt, argq));
    }

    /**
     * Converts an argument containing a string "num,num" into a Dimension2D
     * @param arg The argument
     * @return The point
     * @throws TclException on parse error
     */
    public Dimension2D toDim(TclObject arg) throws TclException {
        var tokens = arg.toString().split(",");
        double x;
        double y;

        // TODO: Fix up error messages to be more helpful and Tcl-like
        if (tokens.length == 2) {
            try {
                x = Double.parseDouble(tokens[0]);
            } catch (Exception ex) {
                throw expected("width,height", arg);
            }

            try {
                y = Double.parseDouble(tokens[1]);
            } catch (Exception ex) {
                throw expected("width,height", arg);
            }

            return new Dimension2D(x, y);
        } else {
            throw expected("width,height", arg);
        }
    }

    public Dimension2D toDim(String opt, Argq argq) throws TclException {
        return toDim(toOptArg(opt, argq));
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

    /**
     * Sets the result, converting a list of Enum symbols into a list of
     * lowercase strings.  Use this to return
     * {@code Enum&lt;?&gt&gt;::values}.
     * @param symbols The symbols
     */
    public void setResult(Enum<?>[] symbols) {
        setResult(Arrays.stream(symbols)
            .map(sym -> sym.toString().toLowerCase())
            .toList());
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

    public TclException unknownOption(String opt) {
        if (opt.startsWith("-")) {
            return badValue("unknown option", opt);
        } else {
            return expected("option", opt);
        }
    }

    public TclException expected(String name, Object value) {
        return new TclEngineException(this,
            "Expected " + name + ", got \"" + value + "\"");
    }

    public TclException badValue(String message, Object value) {
        return new TclEngineException(this,
            message + ": \"" + value + "\"");
    }

    public TclException wrongNumArgs(Argq argq, String argSig) {
        return error("wrong # args: should be \"" +
            commandPrefix(argq) + " " + argSig + "\"");
    }

    public String commandPrefix(Argq argq) {
        var prefix = TclList.newInstance();

        try {
            for (int i = 0; i < argq.getPrefixTokens(); i++) {
                TclList.append(interp, prefix, argq.asCommandArray()[i]);
            }
            return prefix.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("list failure");
        }
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
