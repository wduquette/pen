package pen.tcl;

public interface TclExtension {
    /**
     * Loads the extension's commands into the engine.
     * @param tcl The engine.
     */
    void initialize(TclEngine tcl);

    /**
     * Resets the extension's data.
     */
    void reset();
}
