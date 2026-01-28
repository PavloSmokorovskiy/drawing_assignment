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
                System.out.println("Canvas created: " + width + "x" + height);
            }
        }

        scanner.close();
    }
}
