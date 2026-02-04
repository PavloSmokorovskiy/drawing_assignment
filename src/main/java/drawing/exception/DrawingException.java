package drawing.exception;

/**
 * Unified exception. Unchecked (RuntimeException) for cleaner API.
 */
public final class DrawingException extends RuntimeException {
    public DrawingException(String message) {
        super(message);
    }
}
