package drawing;

import drawing.context.DrawingContext;
import drawing.exception.DrawingException;
import drawing.io.Console;
import drawing.io.SystemConsole;
import drawing.parser.CommandParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Application entry point. Pattern: REPL (Read-Eval-Print Loop).
 * Uses Dependency Injection for Console (testability).
 */
public final class DrawingApp {

    private final Scanner scanner;
    private final boolean interactive;
    private final Console console;
    private final CommandParser parser = new CommandParser();
    private final DrawingContext context;

    public DrawingApp(Scanner scanner, boolean interactive) {
        this(scanner, interactive, new SystemConsole());
    }

    public DrawingApp(Scanner scanner, boolean interactive, Console console) {
        this.scanner = scanner;
        this.interactive = interactive;
        this.console = console;
        this.context = new DrawingContext(console);
    }

    public void run() {
        while (true) {
            if (interactive) {
                console.print("enter command: ");
            }

            if (!scanner.hasNextLine()) {
                break;
            }

            try {
                var line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }

                var command = parser.parse(line);

                if (command.shouldQuit()) {
                    return;
                }

                // Transactional undo: save state before, discard on error
                if (command.modifiesCanvas()) {
                    context.getHistory().saveState(context.getCanvas());
                }

                try {
                    command.execute(context);
                } catch (DrawingException e) {
                    if (command.modifiesCanvas()) {
                        context.getHistory().discardLastState();
                    }
                    throw e;
                }

                if (context.getCanvas() != null) {
                    console.print(context.getRenderer().render(context.getCanvas()));
                }

            } catch (DrawingException e) {
                console.println("Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        var console = new SystemConsole();
        try {
            var source = resolveInput(args);
            try (var stream = source.stream(); var scanner = new Scanner(stream)) {
                new DrawingApp(scanner, source.interactive(), console).run();
            }
        } catch (IOException e) {
            console.printError("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static InputSource resolveInput(String[] args) throws IOException {
        if (args.length == 0) {
            return new InputSource(System.in, true);
        }
        if (args.length == 1) {
            var path = Path.of(args[0]);
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
