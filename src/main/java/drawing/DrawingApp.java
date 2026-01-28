package drawing;

import java.util.Scanner;

public class DrawingApp {
    private static Canvas canvas;
    private static final CanvasRenderer renderer = new CanvasRenderer();

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
                    System.out.print(renderer.render(canvas));
                }

                if (command.equals("L")) {
                    requireCanvas();
                    Point from = new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    Point to = new Point(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                    canvas.validateBounds(from, to);
                    canvas.drawLine(from, to);
                    System.out.print(renderer.render(canvas));
                }

                if (command.equals("R")) {
                    requireCanvas();
                    Point corner1 = new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    Point corner2 = new Point(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                    canvas.validateBounds(corner1, corner2);
                    canvas.drawRectangle(corner1, corner2);
                    System.out.print(renderer.render(canvas));
                }

                if (command.equals("B")) {
                    requireCanvas();
                    Point point = new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    char color = parts[3].charAt(0);
                    canvas.validateBounds(point);
                    canvas.fill(point, color);
                    System.out.print(renderer.render(canvas));
                }
            } catch (DrawingException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void requireCanvas() {
        if (canvas == null) {
            throw new DrawingException("Canvas not created. Use: C <width> <height>");
        }
    }
}
