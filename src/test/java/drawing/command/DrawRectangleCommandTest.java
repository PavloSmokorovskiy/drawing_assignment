package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Point;
import drawing.exception.DrawingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static drawing.canvas.DrawingConstants.EMPTY_CHAR;
import static drawing.canvas.DrawingConstants.LINE_CHAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests DrawRectangleCommand. Verifies edges, corners, and coordinate normalization.
 */
class DrawRectangleCommandTest {

    private DrawingContext context;

    @BeforeEach
    void setUp() {
        context = new DrawingContext();
        new CreateCanvasCommand(10, 6).execute(context);
    }

    @Nested
    class DrawsRectangle {
        @Test
        void drawsAllFourEdges() {
            new DrawRectangleCommand(new Point(2, 2), new Point(5, 4)).execute(context);

            for (int x = 2; x <= 5; x++) {
                assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(x, 2)));
            }
            for (int x = 2; x <= 5; x++) {
                assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(x, 4)));
            }
            for (int y = 2; y <= 4; y++) {
                assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(2, y)));
            }
            for (int y = 2; y <= 4; y++) {
                assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(5, y)));
            }
        }

        @Test
        void insideRemainsEmpty() {
            new DrawRectangleCommand(new Point(2, 2), new Point(5, 4)).execute(context);

            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(3, 3)));
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(4, 3)));
        }

        @Test
        void outsideRemainsEmpty() {
            new DrawRectangleCommand(new Point(2, 2), new Point(5, 4)).execute(context);

            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 1)));
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(6, 5)));
        }
    }

    @Nested
    class ReversedCoordinates {
        @Test
        void handlesBottomRightToTopLeft() {
            new DrawRectangleCommand(new Point(5, 4), new Point(2, 2)).execute(context);

            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(2, 2)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(5, 4)));
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(3, 3)));
        }

        @Test
        void handlesTopRightToBottomLeft() {
            new DrawRectangleCommand(new Point(5, 2), new Point(2, 4)).execute(context);

            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(2, 2)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(5, 4)));
        }

        @Test
        void handlesBottomLeftToTopRight() {
            new DrawRectangleCommand(new Point(2, 4), new Point(5, 2)).execute(context);

            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(2, 2)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(5, 4)));
        }
    }

    @Nested
    class Validation {
        @Test
        void rejectsOutOfBoundsCorner() {
            var cmd = new DrawRectangleCommand(
                    new Point(0, 1), new Point(5, 4));

            assertThrows(DrawingException.class, () -> cmd.execute(context));
        }

        @Test
        void requiresCanvas() {
            var emptyContext = new DrawingContext();
            var cmd = new DrawRectangleCommand(
                    new Point(1, 1), new Point(3, 3));

            assertThrows(DrawingException.class, () -> cmd.execute(emptyContext));
        }
    }

    @Test
    void modifiesCanvas() {
        var cmd = new DrawRectangleCommand(
                new Point(1, 1), new Point(3, 3));
        assertTrue(cmd.modifiesCanvas());
    }
}
