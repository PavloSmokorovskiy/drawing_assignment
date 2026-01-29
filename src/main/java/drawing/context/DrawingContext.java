package drawing.context;

import drawing.canvas.Canvas;
import drawing.canvas.CanvasRenderer;
import drawing.exception.DrawingException;
import drawing.history.CommandHistory;

public final class DrawingContext {

    private Canvas canvas;
    private final CommandHistory history = new CommandHistory();
    private final CanvasRenderer renderer = new CanvasRenderer();

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

    public Canvas requireCanvas() {
        if (canvas == null) {
            throw new DrawingException("Canvas not created. Use: C <width> <height>");
        }
        return canvas;
    }
}
