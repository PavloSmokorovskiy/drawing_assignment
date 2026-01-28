package drawing;

import java.util.Scanner;

public class DrawingApp {
    private final DrawingContext context = new DrawingContext();
    private final CommandParser parser = new CommandParser();
    private final CanvasRenderer renderer = new CanvasRenderer();

    public static void main(String[] args) {
        new DrawingApp().run();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("enter command: ");
            try {
                Command cmd = parser.parse(scanner.nextLine());
                if (cmd.shouldQuit())
                    break;
                cmd.execute(context);
                if (context.getCanvas() != null) {
                    System.out.print(renderer.render(context.getCanvas()));
                }
            } catch (DrawingException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
