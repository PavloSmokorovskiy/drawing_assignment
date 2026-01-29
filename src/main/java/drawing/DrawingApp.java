package drawing;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public final class DrawingApp {

    private final Scanner scanner;
    private final boolean interactive;
    private final CommandParser parser = new CommandParser();
    private final DrawingContext context = new DrawingContext();
    private final CanvasRenderer renderer = new CanvasRenderer();

    public DrawingApp(Scanner scanner, boolean interactive) {
        this.scanner = scanner;
        this.interactive = interactive;
    }

    public void run() {
        while (scanner.hasNextLine()) {
            if (interactive) {
                System.out.print("enter command: ");
            }

            try {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }

                Command command = parser.parse(line);

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
        try {
            InputSource source = resolveInput(args);
            try (var stream = source.stream(); var scanner = new Scanner(stream)) {
                new DrawingApp(scanner, source.interactive()).run();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static InputSource resolveInput(String[] args) throws IOException {
        if (args.length == 0) {
            return new InputSource(System.in, true);
        }
        if (args.length == 1) {
            Path path = Path.of(args[0]);
            if (!Files.exists(path)) {
                throw new IOException("File not found: " + path);
            }
            return new InputSource(Files.newInputStream(path), false);
        }
        throw new IOException("Usage: drawing [input-file]");
    }

    private record InputSource(InputStream stream, boolean interactive) {
    }
}
