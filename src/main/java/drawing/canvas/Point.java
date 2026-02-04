package drawing.canvas;

/**
 * Value Object (DDD). Record provides immutability, equals/hashCode (used in BFS HashSet).
 */
public record Point(int x, int y) {

    public Point moveX(int dx) {
        return new Point(x + dx, y);
    }

    public Point moveY(int dy) {
        return new Point(x, y + dy);
    }
}
