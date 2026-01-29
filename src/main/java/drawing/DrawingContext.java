package drawing;

public final class DrawingContext {

    private Canvas canvas;
    private final CommandHistory history = new CommandHistory();

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public CommandHistory getHistory() {
        return history;
    }

    public Canvas requireCanvas() {
        if (canvas == null) {
            throw new DrawingException("Canvas not created. Use: C <width> <height>");
        }
        return canvas;
    }
}
