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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RedoCommandTest {

    private DrawingContext context;

    @BeforeEach
    void setUp() {
        context = new DrawingContext();
    }

    @Nested
    class RedoOperations {
        @Test
        void redoesLineDrawing() {
            context.getHistory().saveState(null);
            new CreateCanvasCommand(5, 4).execute(context);

            context.getHistory().saveState(context.getCanvas());
            new DrawLineCommand(new Point(1, 1), new Point(3, 1)).execute(context);

            new UndoCommand().execute(context);
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 1)));

            new RedoCommand().execute(context);
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(1, 1)));
        }

        @Test
        void redoesCanvasCreation() {
            context.getHistory().saveState(null);
            new CreateCanvasCommand(5, 4).execute(context);

            new UndoCommand().execute(context);
            assertNull(context.getCanvas());

            new RedoCommand().execute(context);
            assertNotNull(context.getCanvas());
            assertEquals(5, context.getCanvas().width());
        }

        @Test
        void redoesMultipleOperations() {
            context.getHistory().saveState(null);
            new CreateCanvasCommand(5, 4).execute(context);

            context.getHistory().saveState(context.getCanvas());
            new DrawLineCommand(new Point(1, 1), new Point(3, 1)).execute(context);

            context.getHistory().saveState(context.getCanvas());
            new DrawLineCommand(new Point(1, 2), new Point(3, 2)).execute(context);

            new UndoCommand().execute(context);
            new UndoCommand().execute(context);

            new RedoCommand().execute(context);
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(1, 1)));
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 2)));

            new RedoCommand().execute(context);
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(1, 2)));
        }
    }

    @Nested
    class RedoAfterNewCommand {
        @Test
        void newCommandClearsRedoStack() {
            context.getHistory().saveState(null);
            new CreateCanvasCommand(5, 4).execute(context);

            context.getHistory().saveState(context.getCanvas());
            new DrawLineCommand(new Point(1, 1), new Point(3, 1)).execute(context);

            new UndoCommand().execute(context);

            context.getHistory().saveState(context.getCanvas());
            new DrawLineCommand(new Point(1, 2), new Point(3, 2)).execute(context);

            assertThrows(DrawingException.class, () -> new RedoCommand().execute(context));
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void throwsWhenNothingToRedo() {
            context.getHistory().saveState(null);
            new CreateCanvasCommand(5, 4).execute(context);

            assertThrows(DrawingException.class, () -> new RedoCommand().execute(context));
        }
    }

    @Test
    void doesNotModifyCanvas() {
        var cmd = new RedoCommand();
        assertFalse(cmd.modifiesCanvas());
    }

    @Test
    void doesNotQuit() {
        var cmd = new RedoCommand();
        assertFalse(cmd.shouldQuit());
    }
}
