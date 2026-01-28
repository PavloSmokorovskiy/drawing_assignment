package drawing;

import java.util.Scanner;

public class DrawingApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("enter command: ");
            String input = scanner.nextLine();
            String[] parts = input.trim().split("\\s+");
            String command = parts[0].toUpperCase();

            if (command.equals("Q"))
                break;
            System.out.println("Command: " + command + ", args: " + (parts.length - 1));
        }

        scanner.close();
    }
}
