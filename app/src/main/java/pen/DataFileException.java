package pen;

import pen.tcl.TclEngineException;

public class DataFileException extends Exception {
    public DataFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getDetails() {
        return switch (getCause()) {
            case TclEngineException ex -> ex.getErrorInfo();
            case Exception ex -> ex.getMessage();
            default -> "";
        };
    }
}
