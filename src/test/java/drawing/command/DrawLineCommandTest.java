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

class DrawLineCommandTest {

    private DrawingContext context;

    @BeforeEach
    void setUp() {
        context = new DrawingContext();
        new CreateCanvasCommand(5, 4).execute(context);
    }

    @Nested
    class HorizontalLine {
        @Test
        void drawsFromLeftToRight() {
            new DrawLineCommand(new Point(1, 2), new Point(4, 2)).execute(context);

            for (var x = 1; x <= 4; x++) {
                assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(x, 2)));
            }
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(5, 2)));
        }

        @Test
        void drawsFromRightToLeft() {
            new DrawLineCommand(new Point(4, 2), new Point(1, 2)).execute(context);

            for (var x = 1; x <= 4; x++) {
                assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(x, 2)));
            }
        }
    }

    @Nested
    class VerticalLine {
        @Test
        void drawsFromTopToBottom() {
            new DrawLineCommand(new Point(2, 1), new Point(2, 4)).execute(context);

            for (var y = 1; y <= 4; y++) {
                assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(2, y)));
            }
        }

        @Test
        void drawsFromBottomToTop() {
            new DrawLineCommand(new Point(2, 4), new Point(2, 1)).execute(context);

            for (var y = 1; y <= 4; y++) {
                assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(2, y)));
            }
        }
    }

    @Nested
    class DiagonalLine {
        @Test
        void rejectsDiagonalLine() {
            var cmd = new DrawLineCommand(new Point(1, 1), new Point(3, 3));

            var ex = assertThrows(DrawingException.class,
                    () -> cmd.execute(context));
            assertTrue(ex.getMessage().contains("horizontal") || ex.getMessage().contains("vertical"));
        }
    }

    @Nested
    class Validation {
        @Test
        void rejectsOutOfBoundsStart() {
            var cmd = new DrawLineCommand(new Point(0, 1), new Point(3, 1));

            assertThrows(DrawingException.class, () -> cmd.execute(context));
        }

        @Test
        void rejectsOutOfBoundsEnd() {
            var cmd = new DrawLineCommand(new Point(1, 1), new Point(6, 1));

            assertThrows(DrawingException.class, () -> cmd.execute(context));
        }

        @Test
        void requiresCanvas() {
            var emptyContext = new DrawingContext();
            var cmd = new DrawLineCommand(new Point(1, 1), new Point(3, 1));

            var ex = assertThrows(DrawingException.class,
                    () -> cmd.execute(emptyContext));
            assertTrue(ex.getMessage().contains("Canvas"));
        }
    }

    @Test
    void modifiesCanvas() {
        var cmd = new DrawLineCommand(new Point(1, 1), new Point(3, 1));
        assertTrue(cmd.modifiesCanvas());
    }
}
