package pen;

/**
 * Application errors (e.g., input errors) to be reported to the
 * user.
 */
public class AppError extends RuntimeException {
    /**
     * Creates an AppError exception: an error to be presented to the user.
     * @param message The message
     */
    public AppError(String message) {
        super(message);
    }
}
