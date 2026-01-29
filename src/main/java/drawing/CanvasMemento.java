package drawing;

public record CanvasMemento(int width, int height, char[][] pixels) {

    public CanvasMemento {
        char[][] copy = new char[pixels.length][];
        for (int i = 0; i < pixels.length; i++) {
            copy[i] = pixels[i].clone();
        }
        pixels = copy;
    }

    public static CanvasMemento from(Canvas canvas) {
        return new CanvasMemento(canvas.width(), canvas.height(), canvas.pixels());
    }

    public Canvas restore() {
        char[][] copy = new char[pixels.length][];
        for (int i = 0; i < pixels.length; i++) {
            copy[i] = pixels[i].clone();
        }
        return new Canvas(width, height, copy);
    }
}
