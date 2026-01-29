package drawing;

import java.util.Scanner;

public final class DrawingApp {

    private final Scanner scanner;
    private final CommandParser parser = new CommandParser();
    private final DrawingContext context = new DrawingContext();
    private final CanvasRenderer renderer = new CanvasRenderer();

    public DrawingApp(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            System.out.print("enter command: ");

            try {
                Command command = parser.parse(scanner.nextLine());

                if (command.shouldQuit()) {
                    return;
                }

                if (command.modifiesCanvas()) {
                    context.getHistory().saveState(context.getCanvas());
                }

                command.execute(context);

                if (context.getCanvas() != null) {
                    System.out.print(renderer.render(context.getCanvas()));
                }

            } catch (DrawingException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try (var scanner = new Scanner(System.in)) {
            new DrawingApp(scanner).run();
        }
    }
}
