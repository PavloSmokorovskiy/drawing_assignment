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

class BucketFillCommandTest {

    private DrawingContext context;

    @BeforeEach
    void setUp() {
        context = new DrawingContext();
        new CreateCanvasCommand(5, 4).execute(context);
    }

    @Nested
    class FillArea {
        @Test
        void fillsEntireEmptyCanvas() {
            new BucketFillCommand(new Point(1, 1), 'o').execute(context);

            for (int y = 1; y <= 4; y++) {
                for (int x = 1; x <= 5; x++) {
                    assertEquals('o', context.getCanvas().getPixel(new Point(x, y)));
                }
            }
        }

        @Test
        void fillsBoundedArea() {
            new DrawLineCommand(new Point(3, 1), new Point(3, 4)).execute(context);
            new BucketFillCommand(new Point(1, 1), 'o').execute(context);

            assertEquals('o', context.getCanvas().getPixel(new Point(1, 1)));
            assertEquals('o', context.getCanvas().getPixel(new Point(2, 3)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(3, 2)));
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(4, 1)));
        }

        @Test
        void fillInsideRectangle() {
            new DrawRectangleCommand(new Point(1, 1), new Point(5, 4)).execute(context);
            new BucketFillCommand(new Point(3, 2), 'o').execute(context);

            assertEquals('o', context.getCanvas().getPixel(new Point(2, 2)));
            assertEquals('o', context.getCanvas().getPixel(new Point(4, 3)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(1, 1)));
        }

        @Test
        void fillWithSameColorIsNoOp() {
            new BucketFillCommand(new Point(1, 1), 'o').execute(context);
            new BucketFillCommand(new Point(3, 3), 'o').execute(context);

            for (int y = 1; y <= 4; y++) {
                for (int x = 1; x <= 5; x++) {
                    assertEquals('o', context.getCanvas().getPixel(new Point(x, y)));
                }
            }
        }
    }

    @Nested
    class FillOnExistingContent {
        @Test
        void fillOnLineReplacesLineColor() {
            new DrawLineCommand(new Point(1, 2), new Point(5, 2)).execute(context);
            new BucketFillCommand(new Point(3, 2), 'o').execute(context);

            for (int x = 1; x <= 5; x++) {
                assertEquals('o', context.getCanvas().getPixel(new Point(x, 2)));
            }
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 1)));
        }
    }

    @Nested
    class Validation {
        @Test
        void rejectsOutOfBoundsPoint() {
            BucketFillCommand cmd = new BucketFillCommand(new Point(0, 1), 'o');

            assertThrows(DrawingException.class, () -> cmd.execute(context));
        }

        @Test
        void requiresCanvas() {
            DrawingContext emptyContext = new DrawingContext();
            BucketFillCommand cmd = new BucketFillCommand(new Point(1, 1), 'o');

            assertThrows(DrawingException.class, () -> cmd.execute(emptyContext));
        }

        @Test
        void rejectsLineCharAsColor() {
            BucketFillCommand cmd = new BucketFillCommand(new Point(1, 1), LINE_CHAR);

            DrawingException ex = assertThrows(DrawingException.class, () -> cmd.execute(context));
            assertTrue(ex.getMessage().contains("reserved for lines"));
        }
    }

    @Test
    void modifiesCanvas() {
        BucketFillCommand cmd = new BucketFillCommand(new Point(1, 1), 'o');
        assertTrue(cmd.modifiesCanvas());
    }
}
