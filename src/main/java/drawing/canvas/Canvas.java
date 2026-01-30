package drawing.canvas;

import drawing.exception.DrawingException;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import static drawing.canvas.DrawingConstants.EMPTY_CHAR;
import static drawing.canvas.DrawingConstants.LINE_CHAR;

public final class Canvas {

    private final int width;
    private final int height;
    private final char[][] pixels;

    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new char[height][width];
        clear();
    }

    Canvas(int width, int height, char[][] pixels) {
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

    char[][] copyPixels() {
        return PixelArrays.copy(pixels);
    }

    char getPixelRaw(int x, int y) {
        return pixels[y][x];
    }

    private boolean isOutOfBounds(Point p) {
        return p.x() < 1 || p.x() > width || p.y() < 1 || p.y() > height;
    }

    public void validateBounds(Point... points) {
        for (Point p : points) {
            if (isOutOfBounds(p)) {
                throw new DrawingException(
                        "Point (%d,%d) out of bounds (canvas: %dx%d)"
                                .formatted(p.x(), p.y(), width, height));
            }
        }
    }

    public char getPixel(Point p) {
        return pixels[p.y() - 1][p.x() - 1];
    }

    public void setPixel(Point p, char c) {
        pixels[p.y() - 1][p.x() - 1] = c;
    }

    public void drawLine(Point from, Point to) {
        int x1 = Math.min(from.x(), to.x());
        int x2 = Math.max(from.x(), to.x());
        int y1 = Math.min(from.y(), to.y());
        int y2 = Math.max(from.y(), to.y());

        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                setPixel(new Point(x, y), LINE_CHAR);
            }
        }
    }

    public void fill(Point start, char color) {
        char target = getPixel(start);
        if (target == color) {
            return;
        }

        var queue = new ArrayDeque<Point>();
        Set<Point> visited = new HashSet<>();
        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            setPixel(p, color);

            addIfNotVisited(queue, visited, p.moveX(1), target);
            addIfNotVisited(queue, visited, p.moveX(-1), target);
            addIfNotVisited(queue, visited, p.moveY(1), target);
            addIfNotVisited(queue, visited, p.moveY(-1), target);
        }
    }

    private void addIfNotVisited(ArrayDeque<Point> queue, Set<Point> visited, Point p, char target) {
        if (!isOutOfBounds(p) && !visited.contains(p) && getPixel(p) == target) {
            queue.offer(p);
            visited.add(p);
        }
    }

    private void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = EMPTY_CHAR;
            }
        }
    }
}
