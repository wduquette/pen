package pen.tcl;

import tcl.lang.TclException;

/**
 * An extended TclException which retains the error message and an optional
 * cause.
 */
public class TclEngineException extends TclException {
    private final Throwable cause;
    private final String message;

    public TclEngineException(TclEngine engine, String message) {
        this(engine, message, null);
    }

    public TclEngineException(TclEngine engine, String message, Throwable cause) {
        super(engine.interp(), message);
        // NOTE: TclException does NOT set the exception's message string, but
        // rather puts it in the interp's result.
        this.message = message;
        this.cause = cause;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }

    public String toString() {
        return getClass().getName() + " " + getMessage();
    }
}
