package pen.tcl;

import tcl.lang.TclException;

public interface TclEngineProc {
    /**
     * Processes the argument queue in the context of the engine
     * @param engine The TclEngine
     * @param argq The argument queue
     * @throws TclException on error
     */
    void run(TclEngine engine, Argq argq) throws TclException;
}
