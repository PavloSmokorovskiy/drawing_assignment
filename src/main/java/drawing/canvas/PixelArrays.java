package drawing.canvas;

/**
 * Utility class. Deep copy of 2D array for Memento pattern.
 */
final class PixelArrays {

    private PixelArrays() {
    }

    static char[][] copy(char[][] source) {
        var copy = new char[source.length][];
        for (var i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }
}
