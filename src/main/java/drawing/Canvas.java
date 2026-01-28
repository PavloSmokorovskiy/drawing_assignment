package drawing;

import java.util.LinkedList;
import java.util.Queue;

import static drawing.DrawingConstants.*;

public class Canvas {
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

    public char getPixel(Point p) {
        return pixels[p.y() - 1][p.x() - 1];
    }

    public void setPixel(Point p, char c) {
        pixels[p.y() - 1][p.x() - 1] = c;
    }

    public void drawLine(Point from, Point to) {
        int minX = Math.min(from.x(), to.x()), maxX = Math.max(from.x(), to.x());
        int minY = Math.min(from.y(), to.y()), maxY = Math.max(from.y(), to.y());
        for (int y = minY; y <= maxY; y++)
            for (int x = minX; x <= maxX; x++)
                setPixel(new Point(x, y), LINE_CHAR);
    }

    public void drawRectangle(Point corner1, Point corner2) {
        drawLine(new Point(corner1.x(), corner1.y()), new Point(corner2.x(), corner1.y())); // верх
        drawLine(new Point(corner1.x(), corner2.y()), new Point(corner2.x(), corner2.y())); // низ
        drawLine(new Point(corner1.x(), corner1.y()), new Point(corner1.x(), corner2.y())); // лево
        drawLine(new Point(corner2.x(), corner1.y()), new Point(corner2.x(), corner2.y())); // право
    }

    public void fill(Point start, char color) {
        char target = getPixel(start);
        if (target == color)
            return;

        Queue<Point> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Point p = queue.poll();

            if (isOutOfBounds(p))
                continue;
            if (getPixel(p) != target)
                continue;

            setPixel(p, color);

            queue.add(p.moveX(1));
            queue.add(p.moveX(-1));
            queue.add(p.moveY(1));
            queue.add(p.moveY(-1));
        }
    }

    public boolean isOutOfBounds(Point p) {
        return p.x() < 1 || p.x() > width || p.y() < 1 || p.y() > height;
    }

    public void validateBounds(Point... points) {
        for (Point p : points) {
            if (isOutOfBounds(p)) {
                throw new DrawingException("Coordinates out of bounds");
            }
        }
    }

    private void clear() {
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                pixels[y][x] = EMPTY_CHAR;
    }
}
