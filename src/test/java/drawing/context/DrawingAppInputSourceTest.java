package drawing.context;

import drawing.DrawingApp;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests REPL input modes: interactive (stdin) and file (batch). Uses @TempDir (JUnit 5).
 */
class DrawingAppInputSourceTest {

    @Nested
    class InteractiveMode {
        @Test
        void showsPromptInInteractiveMode() {
            var input = new ByteArrayInputStream("C 5 4\nQ\n".getBytes());
            var output = new ByteArrayOutputStream();
            System.setOut(new PrintStream(output));

            var scanner = new Scanner(input);
            new DrawingApp(scanner, true).run();

            var result = output.toString();
            assertTrue(result.contains("enter command:"));
        }
    }

    @Nested
    class FileInputMode {
        @Test
        void executesCommandsFromFile(@TempDir Path tempDir) throws Exception {
            var inputFile = tempDir.resolve("commands.txt");
            Files.writeString(inputFile, """
                C 10 4
                L 1 1 5 1
                Q
                """);

            var output = new ByteArrayOutputStream();
            System.setOut(new PrintStream(output));

            var scanner = new Scanner(Files.newInputStream(inputFile));
            new DrawingApp(scanner, false).run();

            var result = output.toString();
            assertFalse(result.contains("enter command:"));
            assertTrue(result.contains("------------"));
        }

        @Test
        void handlesBlankLinesInFile(@TempDir Path tempDir) throws Exception {
            var inputFile = tempDir.resolve("commands.txt");
            Files.writeString(inputFile, """
                C 5 4

                L 1 1 3 1

                Q
                """);

            var scanner = new Scanner(Files.newInputStream(inputFile));
            var app = new DrawingApp(scanner, false);

            assertDoesNotThrow(app::run);
        }

        @Test
        void stopsAtEndOfFile(@TempDir Path tempDir) throws Exception {
            var inputFile = tempDir.resolve("commands.txt");
            Files.writeString(inputFile, """
                C 5 4
                L 1 1 3 1
                """);

            var scanner = new Scanner(Files.newInputStream(inputFile));
            var app = new DrawingApp(scanner, false);

            assertDoesNotThrow(app::run);
        }
    }

    @Nested
    class ErrorsInFileMode {
        @Test
        void continuesAfterErrorInFile(@TempDir Path tempDir) throws Exception {
            var inputFile = tempDir.resolve("commands.txt");
            Files.writeString(inputFile, """
                C 5 4
                L 0 0 3 1
                L 1 1 3 1
                Q
                """);

            var output = new ByteArrayOutputStream();
            System.setOut(new PrintStream(output));

            var scanner = new Scanner(Files.newInputStream(inputFile));
            new DrawingApp(scanner, false).run();

            var result = output.toString();
            assertTrue(result.contains("Error:"));
            assertTrue(result.contains("xxx"));
        }
    }
}
