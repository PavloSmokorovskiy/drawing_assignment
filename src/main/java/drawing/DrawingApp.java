package drawing;

import java.util.Scanner;

public class DrawingApp {
    private static Canvas canvas;

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
                    int width = Integer.parseInt(parts[1]);
                    int height = Integer.parseInt(parts[2]);
                    canvas = new Canvas(width, height);
                    printCanvas();
                }

                if (command.equals("L")) {
                    requireCanvas();
                    int x1 = Integer.parseInt(parts[1]);
                    int y1 = Integer.parseInt(parts[2]);
                    int x2 = Integer.parseInt(parts[3]);
                    int y2 = Integer.parseInt(parts[4]);
                    canvas.validateBounds(x1, y1);
                    canvas.validateBounds(x2, y2);
                    canvas.drawLine(x1, y1, x2, y2);
                    printCanvas();
                }

                if (command.equals("R")) {
                    requireCanvas();
                    int x1 = Integer.parseInt(parts[1]);
                    int y1 = Integer.parseInt(parts[2]);
                    int x2 = Integer.parseInt(parts[3]);
                    int y2 = Integer.parseInt(parts[4]);
                    canvas.validateBounds(x1, y1);
                    canvas.validateBounds(x2, y2);
                    canvas.drawRectangle(x1, y1, x2, y2);
                    printCanvas();
                }

                if (command.equals("B")) {
                    requireCanvas();
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    char color = parts[3].charAt(0);
                    canvas.validateBounds(x, y);
                    canvas.fill(x, y, color);
                    printCanvas();
                }
            } catch (DrawingException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void printCanvas() {
        int w = canvas.width();
        int h = canvas.height();

        System.out.println("-".repeat(w + 2));

        for (int y = 0; y < h; y++) {
            System.out.print("|");
            for (int x = 0; x < w; x++) {
                System.out.print(canvas.pixels()[y][x]);
            }
            System.out.println("|");
        }

        System.out.println("-".repeat(w + 2));
    }

    private static void requireCanvas() {
        if (canvas == null) {
            throw new DrawingException("Canvas not created. Use: C <width> <height>");
        }
    }
}
