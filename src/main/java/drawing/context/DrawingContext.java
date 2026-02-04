package drawing.context;

import drawing.canvas.Canvas;
import drawing.canvas.CanvasRenderer;
import drawing.exception.DrawingException;
import drawing.history.CommandHistory;
import drawing.io.Console;
import drawing.io.SystemConsole;

/**
 * Pattern: Context Object. Aggregates session state for commands.
 * Dependency Injection via constructor for testability.
 */
public final class DrawingContext {

    private Canvas canvas;
    private final CommandHistory history = new CommandHistory();
    private final CanvasRenderer renderer = new CanvasRenderer();
    private final Console console;

    public DrawingContext() {
        this(new SystemConsole());
    }

    public DrawingContext(Console console) {
        this.console = console;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public CommandHistory getHistory() {
        return history;
    }

    public CanvasRenderer getRenderer() {
        return renderer;
    }

    public Console getConsole() {
        return console;
    }

    public Canvas requireCanvas() {
        if (canvas == null) {
            throw new DrawingException("Canvas not created. Use: C <width> <height>");
        }
        return canvas;
    }
}
