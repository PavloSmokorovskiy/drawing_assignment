package drawing;

import java.util.LinkedList;
import java.util.Queue;

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

    public void drawLine(int x1, int y1, int x2, int y2) {
        int minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2), maxY = Math.max(y1, y2);
        for (int y = minY; y <= maxY; y++)
            for (int x = minX; x <= maxX; x++)
                pixels[y - 1][x - 1] = 'x';
    }

    public void drawRectangle(int x1, int y1, int x2, int y2) {
        drawLine(x1, y1, x2, y1);
        drawLine(x1, y2, x2, y2);
        drawLine(x1, y1, x1, y2);
        drawLine(x2, y1, x2, y2);
    }

    public void fill(int startX, int startY, char color) {
        char target = pixels[startY - 1][startX - 1];
        if (target == color)
            return;

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[] { startX, startY });

        while (!queue.isEmpty()) {
            int[] p = queue.poll();
            int x = p[0], y = p[1];

            if (x < 1 || x > width || y < 1 || y > height)
                continue;
            if (pixels[y - 1][x - 1] != target)
                continue;

            pixels[y - 1][x - 1] = color;

            queue.add(new int[] { x + 1, y });
            queue.add(new int[] { x - 1, y });
            queue.add(new int[] { x, y + 1 });
            queue.add(new int[] { x, y - 1 });
        }
    }

    public boolean isOutOfBounds(int x, int y) {
        return x < 1 || x > width || y < 1 || y > height;
    }

    public void validateBounds(int x, int y) {
        if (isOutOfBounds(x, y)) {
            throw new DrawingException("Coordinates out of bounds");
        }
    }

    private void clear() {
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                pixels[y][x] = ' ';
    }
}
