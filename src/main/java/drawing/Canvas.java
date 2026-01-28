package drawing;

import java.util.ArrayDeque;

import static drawing.DrawingConstants.EMPTY_CHAR;
import static drawing.DrawingConstants.LINE_CHAR;

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

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public char[][] pixels() {
        return pixels;
    }

    boolean isOutOfBounds(Point p) {
        return p.x() < 1 || p.x() > width || p.y() < 1 || p.y() > height;
    }

    public void validateBounds(Point... points) {
        for (Point p : points) {
            if (isOutOfBounds(p)) {
                throw new DrawingException("Coordinates out of canvas bounds");
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

    public void drawRectangle(Point a, Point b) {
        Point topRight = new Point(b.x(), a.y());
        Point bottomLeft = new Point(a.x(), b.y());

        drawLine(a, topRight);
        drawLine(bottomLeft, b);
        drawLine(a, bottomLeft);
        drawLine(topRight, b);
    }

    public void fill(Point start, char color) {
        char target = getPixel(start);
        if (target == color) {
            return;
        }

        var queue = new ArrayDeque<Point>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Point p = queue.poll();

            if (isOutOfBounds(p) || getPixel(p) != target) {
                continue;
            }

            setPixel(p, color);

            addIfValid(queue, p.moveX(1), target);
            addIfValid(queue, p.moveX(-1), target);
            addIfValid(queue, p.moveY(1), target);
            addIfValid(queue, p.moveY(-1), target);
        }
    }

    private void addIfValid(ArrayDeque<Point> queue, Point p, char target) {
        if (!isOutOfBounds(p) && getPixel(p) == target) {
            queue.add(p);
        }
    }

    private void clear() {
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                pixels[y][x] = EMPTY_CHAR;
    }
}
