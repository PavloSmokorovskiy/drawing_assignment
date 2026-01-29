package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Canvas;
import drawing.canvas.Point;
import drawing.exception.DrawingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SaveCommandTest {

    private DrawingContext context;

    @BeforeEach
    void setUp() {
        context = new DrawingContext();
    }

    @Nested
    class Saving {
        @Test
        void savesCanvasToFile(@TempDir Path tempDir) throws Exception {
            context.setCanvas(new Canvas(5, 3));
            context.getCanvas().drawLine(new Point(1, 1), new Point(5, 1));

            Path outputFile = tempDir.resolve("output.txt");
            new SaveCommand(outputFile.toString()).execute(context);

            assertTrue(Files.exists(outputFile));
            String content = Files.readString(outputFile);
            assertTrue(content.contains("xxxxx"));
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
