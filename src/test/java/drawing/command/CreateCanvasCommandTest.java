package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Point;
import drawing.exception.DrawingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static drawing.canvas.DrawingConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateCanvasCommandTest {

    private DrawingContext context;

    @BeforeEach
    void setUp() {
        context = new DrawingContext();
    }

    @Nested
    class Creation {
        @Test
        void createsCanvasWithCorrectDimensions() {
            new CreateCanvasCommand(20, 4).execute(context);

            assertNotNull(context.getCanvas());
            assertEquals(20, context.getCanvas().width());
            assertEquals(4, context.getCanvas().height());
        }

        @Test
        void createsEmptyCanvas() {
            new CreateCanvasCommand(5, 4).execute(context);

            for (int y = 1; y <= 4; y++) {
                for (int x = 1; x <= 5; x++) {
                    assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(x, y)));
                }
            }
        }

        @Test
        void replacesExistingCanvas() {
            new CreateCanvasCommand(5, 4).execute(context);
            new DrawLineCommand(new Point(1, 1), new Point(3, 1)).execute(context);

            new CreateCanvasCommand(10, 8).execute(context);

            assertEquals(10, context.getCanvas().width());
            assertEquals(8, context.getCanvas().height());
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 1)));
        }
    }

    @Nested
    class Validation {
        @Test
        void acceptsMaximumSize() {
            assertDoesNotThrow(() ->
                    new CreateCanvasCommand(MAX_CANVAS_WIDTH, MAX_CANVAS_HEIGHT).execute(context));
        }

        @Test
        void rejectsWidthExceedingMaximum() {
            CreateCanvasCommand cmd = new CreateCanvasCommand(MAX_CANVAS_WIDTH + 1, 10);

            DrawingException ex = assertThrows(DrawingException.class, () -> cmd.execute(context));
            assertTrue(ex.getMessage().contains("exceeds maximum"));
        }

        @Test
        void rejectsHeightExceedingMaximum() {
            CreateCanvasCommand cmd = new CreateCanvasCommand(10, MAX_CANVAS_HEIGHT + 1);

            DrawingException ex = assertThrows(DrawingException.class, () -> cmd.execute(context));
            assertTrue(ex.getMessage().contains("exceeds maximum"));
        }
    }

    @Test
    void modifiesCanvas() {
        CreateCanvasCommand cmd = new CreateCanvasCommand(5, 4);
        assertTrue(cmd.modifiesCanvas());
    }

    @Test
    void doesNotQuit() {
        CreateCanvasCommand cmd = new CreateCanvasCommand(5, 4);
        assertFalse(cmd.shouldQuit());
    }
}
