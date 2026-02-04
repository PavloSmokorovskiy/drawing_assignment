package drawing.io;

/**
 * Pattern: Strategy (GoF). Abstracts output for testability.
 * Principle: Dependency Inversion (SOLID).
 */
public interface Console {

    void print(String message);

    void println(String message);

    void printError(String message);
}
