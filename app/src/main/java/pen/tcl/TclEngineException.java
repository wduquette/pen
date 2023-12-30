package pen.tcl;

import tcl.lang.TclException;

/**
 * An extended TclException which retains the error message and an optional
 * cause.
 */
public class TclEngineException extends TclException {
    private final Throwable cause;
    private final String message;
    private final String errorInfo;
    private final int errorLine;

    public TclEngineException(TclEngine engine, String message) {
        this(engine, message, null);
    }

    public TclEngineException(TclEngine engine, String message, Throwable cause) {
        super(engine.interp(), message);
        this.message = message;
        this.cause = cause;
        this.errorInfo = "n/a";
        this.errorLine = 1;
    }

    public TclEngineException(TclEngine engine, TclException cause) {
        super(engine.interp(), engine.interp().getResult().toString());
        this.message = engine.interp().getResult().toString();
        this.cause = cause;
        this.errorInfo = engine.getErrorInfo();
        this.errorLine = engine.getErrorLine();
    }

    public String getMessage() {
        return message;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public int getErrorLine() {
        return errorLine;
    }

    public Throwable getCause() {
        return cause;
    }

    public String toString() {
        return getClass().getName() + " " + getMessage();
    }
}
