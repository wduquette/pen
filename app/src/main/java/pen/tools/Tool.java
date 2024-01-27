package pen.tools;

/**
 * Base type for tools.  Provides default methods for standard helpers.
 */
public interface Tool {
    /**
     * Returns the tool's metadata.
     * @return The metadata
     */
    ToolInfo toolInfo();

    /**
     * Handles uncaught exceptions for the tool.  By default:
     *
     * <ul>
     * <li>{@link ToolException} results in a nice error message.</li>
     * <li>Other throwables result in an "Unexpected exception" message
     *     and a stack trace.</li>
     * <li>Either way, the tool terminates.</li>
     * </ul>
     *
     * <p>Subclasses may override this to provide any desired behavior.</p>
     *
     * @param onRun Whether this occurred in the run method or later
     * @param ex The exception
     */
    @SuppressWarnings("unused")
    default void handleUncaughtException(boolean onRun, Throwable ex) {
        if (ex instanceof ToolException tex) {
            System.err.println(toolInfo().name() + ": " + ex.getMessage());
            if (tex.getCause() != null) {
                System.err.println("   *** " + tex.getCause().getMessage());
            }
            System.exit(1);
        } else {
            System.err.println(toolInfo().name() + ": Unexpected exception,");
            ex.printStackTrace(System.err);
        }
    }
}
