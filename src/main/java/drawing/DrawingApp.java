package drawing;

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
                int x1 = Integer.parseInt(parts[1]);
                int y1 = Integer.parseInt(parts[2]);
                int x2 = Integer.parseInt(parts[3]);
                int y2 = Integer.parseInt(parts[4]);
                drawLine(x1, y1, x2, y2);
                printCanvas();
            }

            if (command.equals("R")) {
                int x1 = Integer.parseInt(parts[1]);
                int y1 = Integer.parseInt(parts[2]);
                int x2 = Integer.parseInt(parts[3]);
                int y2 = Integer.parseInt(parts[4]);
                drawLine(x1, y1, x2, y1); // верх
                drawLine(x1, y2, x2, y2); // низ
                drawLine(x1, y1, x1, y2); // лево
                drawLine(x2, y1, x2, y2); // право
                printCanvas();
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
}
