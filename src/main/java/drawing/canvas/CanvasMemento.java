package drawing.canvas;

public final class CanvasMemento {

    private final int width;
    private final int height;
    private final char[][] pixels;

    private CanvasMemento(int width, int height, char[][] pixels) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public static CanvasMemento from(Canvas canvas) {
        return new CanvasMemento(canvas.width(), canvas.height(), canvas.copyPixels());
    }

    public Canvas restore() {
        return new Canvas(width, height, copyPixels());
    }

    private char[][] copyPixels() {
        char[][] copy = new char[pixels.length][];
        for (int i = 0; i < pixels.length; i++) {
            copy[i] = pixels[i].clone();
        }
        return copy;
    }
}
