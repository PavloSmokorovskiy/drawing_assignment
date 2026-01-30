package drawing.canvas;

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
