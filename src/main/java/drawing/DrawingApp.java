package drawing;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class DrawingApp {
    private static char[][] canvas;
    private static int width, height;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("enter command: ");
            String input = scanner.nextLine();
            String[] parts = input.trim().split("\\s+");
            String command = parts[0].toUpperCase();

            if (command.equals("Q"))
                break;

            try {
                if (command.equals("C")) {
                    width = Integer.parseInt(parts[1]);
                    height = Integer.parseInt(parts[2]);
                    canvas = new char[height][width];
                    for (int y = 0; y < height; y++)
                        for (int x = 0; x < width; x++)
                            canvas[y][x] = ' ';
                    printCanvas();
                }

                if (command.equals("L")) {
                    requireCanvas();
                    int x1 = Integer.parseInt(parts[1]);
                    int y1 = Integer.parseInt(parts[2]);
                    int x2 = Integer.parseInt(parts[3]);
                    int y2 = Integer.parseInt(parts[4]);
                    validateBounds(x1, y1);
                    validateBounds(x2, y2);
                    drawLine(x1, y1, x2, y2);
                    printCanvas();
                }

                if (command.equals("R")) {
                    requireCanvas();
                    int x1 = Integer.parseInt(parts[1]);
                    int y1 = Integer.parseInt(parts[2]);
                    int x2 = Integer.parseInt(parts[3]);
                    int y2 = Integer.parseInt(parts[4]);
                    validateBounds(x1, y1);
                    validateBounds(x2, y2);
                    drawLine(x1, y1, x2, y1);
                    drawLine(x1, y2, x2, y2);
                    drawLine(x1, y1, x1, y2);
                    drawLine(x2, y1, x2, y2);
                    printCanvas();
                }

                if (command.equals("B")) {
                    requireCanvas();
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    char color = parts[3].charAt(0);
                    validateBounds(x, y);
                    fill(x, y, color);
                    printCanvas();
                }
            } catch (DrawingException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void printCanvas() {
        System.out.println("-".repeat(width + 2));

        for (int y = 0; y < height; y++) {
            System.out.print("|");
            for (int x = 0; x < width; x++) {
                System.out.print(canvas[y][x]);
            }
            System.out.println("|");
        }

        System.out.println("-".repeat(width + 2));
    }

    private static void drawLine(int x1, int y1, int x2, int y2) {
        int minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2), maxY = Math.max(y1, y2);
        for (int y = minY; y <= maxY; y++)
            for (int x = minX; x <= maxX; x++)
                canvas[y - 1][x - 1] = 'x';
    }

    private static void requireCanvas() {
        if (canvas == null) {
            throw new DrawingException("Canvas not created. Use: C <width> <height>");
        }
    }

    private static void validateBounds(int x, int y) {
        if (x < 1 || x > width || y < 1 || y > height) {
            throw new DrawingException("Coordinates out of bounds");
        }
    }

    private static void fill(int startX, int startY, char color) {
        char target = canvas[startY - 1][startX - 1];
        if (target == color)
            return;

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[] { startX, startY });

        while (!queue.isEmpty()) {
            int[] p = queue.poll();
            int x = p[0], y = p[1];

            if (x < 1 || x > width || y < 1 || y > height)
                continue;
            if (canvas[y - 1][x - 1] != target)
                continue;

            canvas[y - 1][x - 1] = color;

            queue.add(new int[] { x + 1, y });
            queue.add(new int[] { x - 1, y });
            queue.add(new int[] { x, y + 1 });
            queue.add(new int[] { x, y - 1 });
        }
    }
}
