package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Canvas;
import drawing.canvas.Point;
import drawing.exception.DrawingException;
import drawing.io.TestConsole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaveCommandTest {

    private DrawingContext context;
    private TestConsole console;

    @BeforeEach
    void setUp() {
        console = new TestConsole();
        context = new DrawingContext(console);
    }

    @Nested
    class Saving {
        @Test
        void savesCanvasToFile(@TempDir Path tempDir) throws Exception {
            context.setCanvas(new Canvas(5, 3));
            context.getCanvas().drawLine(new Point(1, 1), new Point(5, 1));

            var outputFile = tempDir.resolve("output.txt");
            new SaveCommand(outputFile.toString()).execute(context);

            assertTrue(Files.exists(outputFile));
            var content = Files.readString(outputFile);
            assertTrue(content.contains("xxxxx"));
        }

        @Test
        void printsConfirmationMessage(@TempDir Path tempDir) {
            context.setCanvas(new Canvas(5, 3));

            var outputFile = tempDir.resolve("output.txt");
            new SaveCommand(outputFile.toString()).execute(context);

            assertTrue(console.getOutput().contains("Canvas saved to:"));
        }

        @Test
        void throwsWhenNoCanvas() {
            assertThrows(DrawingException.class,
                    () -> new SaveCommand("test.txt").execute(context));
        }
    }

    @Nested
    class CommandProperties {
        @Test
        void doesNotModifyCanvas() {
            assertFalse(new SaveCommand("test.txt").modifiesCanvas());
        }

        @Test
        void doesNotQuit() {
            assertFalse(new SaveCommand("test.txt").shouldQuit());
        }
    }
}
