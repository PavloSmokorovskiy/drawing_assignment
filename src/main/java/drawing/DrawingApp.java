package drawing;

import java.util.Scanner;

public class DrawingApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\u001B[33mQ or q for exit");
            System.out.print("\u001B[32mEnter command: ");
            String input = scanner.nextLine();

            if (input.equals("Q") || input.equals("q")) {
                break;
            }

            System.out.println("\u001B[0mYou entered: " + input);
        }

        scanner.close();
    }
}
