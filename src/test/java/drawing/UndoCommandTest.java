package drawing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static drawing.DrawingConstants.EMPTY_CHAR;
import static drawing.DrawingConstants.LINE_CHAR;
import static org.junit.jupiter.api.Assertions.*;

class UndoCommandTest {

    private DrawingContext context;

    @BeforeEach
    void setUp() {
        context = new DrawingContext();
    }

    @Nested
    class UndoOperations {
        @Test
        void undoesLineDrawing() {
            context.getHistory().saveState(null);
            new CreateCanvasCommand(5, 4).execute(context);

            context.getHistory().saveState(context.getCanvas());
            new DrawLineCommand(new Point(1, 1), new Point(3, 1)).execute(context);

            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(1, 1)));

            new UndoCommand().execute(context);

            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 1)));
        }

        @Test
        void undoesCanvasCreationToNull() {
            context.getHistory().saveState(null);
            new CreateCanvasCommand(5, 4).execute(context);

            assertNotNull(context.getCanvas());

            new UndoCommand().execute(context);

            assertNull(context.getCanvas());
        }

        @Test
        void undoesMultipleOperations() {
            context.getHistory().saveState(null);
            new CreateCanvasCommand(5, 4).execute(context);

            context.getHistory().saveState(context.getCanvas());
            new DrawLineCommand(new Point(1, 1), new Point(3, 1)).execute(context);

            context.getHistory().saveState(context.getCanvas());
            new DrawLineCommand(new Point(1, 2), new Point(3, 2)).execute(context);

            new UndoCommand().execute(context);
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 2)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(1, 1)));

            new UndoCommand().execute(context);
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 1)));
        }

        @Test
        void undoesFill() {
            context.getHistory().saveState(null);
            new CreateCanvasCommand(5, 4).execute(context);

            context.getHistory().saveState(context.getCanvas());
            new BucketFillCommand(new Point(1, 1), 'o').execute(context);

            assertEquals('o', context.getCanvas().getPixel(new Point(3, 3)));

            new UndoCommand().execute(context);

            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(3, 3)));
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void throwsWhenNothingToUndo() {
            new CreateCanvasCommand(5, 4).execute(context);

            assertThrows(DrawingException.class, () -> new UndoCommand().execute(context));
        }
    }

    @Test
    void doesNotModifyCanvas() {
        UndoCommand cmd = new UndoCommand();
        assertFalse(cmd.modifiesCanvas());
    }

    @Test
    void doesNotQuit() {
        UndoCommand cmd = new UndoCommand();
        assertFalse(cmd.shouldQuit());
    }
}
