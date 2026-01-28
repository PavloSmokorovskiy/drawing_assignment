package drawing;

public final class DrawingContext {

    private Canvas canvas;

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public Canvas requireCanvas() {
        if (canvas == null) {
            throw new DrawingException("Canvas not created. Use: C <width> <height>");
        }
        return canvas;
    }
}
