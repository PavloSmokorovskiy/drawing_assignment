package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static drawing.canvas.DrawingConstants.EMPTY_CHAR;
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
