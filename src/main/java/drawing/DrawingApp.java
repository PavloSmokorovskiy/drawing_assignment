package drawing;

import java.util.Scanner;

import static drawing.DrawingConstants.*;

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
                    Point from = new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    Point to = new Point(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                    canvas.validateBounds(from, to);
                    canvas.drawLine(from, to);
                    printCanvas();
                }

                if (command.equals("R")) {
                    requireCanvas();
                    Point corner1 = new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    Point corner2 = new Point(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                    canvas.validateBounds(corner1, corner2);
                    canvas.drawRectangle(corner1, corner2);
                    printCanvas();
                }

                if (command.equals("B")) {
                    requireCanvas();
                    Point point = new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    char color = parts[3].charAt(0);
                    canvas.validateBounds(point);
                    canvas.fill(point, color);
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

        System.out.println(String.valueOf(HORIZONTAL_BORDER).repeat(w + 2));

        for (int y = 0; y < h; y++) {
            System.out.print(VERTICAL_BORDER);
            for (int x = 0; x < w; x++) {
                System.out.print(canvas.pixels()[y][x]);
            }
            System.out.println(VERTICAL_BORDER);
        }

        System.out.println(String.valueOf(HORIZONTAL_BORDER).repeat(w + 2));
    }

    private static void requireCanvas() {
        if (canvas == null) {
            throw new DrawingException("Canvas not created. Use: C <width> <height>");
        }
    }
}
