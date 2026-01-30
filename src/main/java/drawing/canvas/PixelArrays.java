package drawing.canvas;

final class PixelArrays {

    private PixelArrays() {
    }

    static char[][] copy(char[][] source) {
        char[][] copy = new char[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }
}
