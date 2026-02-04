package drawing.io;

import java.io.PrintStream;

/**
 * Strategy implementation for production. Wraps System.out/err.
 */
public final class SystemConsole implements Console {

    private final PrintStream out;
    private final PrintStream err;

    public SystemConsole() {
        this(System.out, System.err);
    }

    public SystemConsole(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    @Override
    public void print(String message) {
        out.print(message);
        out.flush();
    }

    @Override
    public void println(String message) {
        out.println(message);
    }

    @Override
    public void printError(String message) {
        err.println(message);
    }
}
