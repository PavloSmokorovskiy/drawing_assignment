package drawing.history;

import drawing.canvas.Canvas;
import drawing.canvas.CanvasMemento;
import drawing.canvas.Point;
import drawing.exception.DrawingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static drawing.canvas.DrawingConstants.LINE_CHAR;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandHistoryTest {

    private CommandHistory history;

    @BeforeEach
    void setUp() {
        history = new CommandHistory();
    }

    @Nested
    class SaveState {
        @Test
        void savesNullCanvasState() {
            history.saveState(null);

            assertTrue(history.canUndo());
        }

        @Test
        void savesCanvasState() {
            var canvas = new Canvas(5, 4);
            history.saveState(canvas);

            assertTrue(history.canUndo());
        }

        @Test
        void clearsRedoStackOnNewSave() {
            var canvas = new Canvas(5, 4);
            history.saveState(canvas);
            history.undo(canvas);

            assertTrue(history.canRedo());

            history.saveState(canvas);

            assertFalse(history.canRedo());
        }
    }

    @Nested
    class Undo {
        @Test
        void throwsWhenNothingToUndo() {
            var canvas = new Canvas(5, 4);

            var ex = assertThrows(DrawingException.class,
                    () -> history.undo(canvas));
            assertTrue(ex.getMessage().contains("undo"));
        }

        @Test
        void returnsNullForNullState() {
            history.saveState(null);
            var canvas = new Canvas(5, 4);

            var result = history.undo(canvas);

            assertNull(result);
        }

        @Test
        void returnsPreviousState() {
            var original = new Canvas(5, 4);
            original.drawLine(new Point(1, 1), new Point(3, 1));
            history.saveState(original);

            var modified = new Canvas(5, 4);
            modified.drawLine(new Point(1, 1), new Point(5, 1));

            var result = history.undo(modified);

            assertNotNull(result);
            var restored = result.restore();
            assertEquals(LINE_CHAR, restored.getPixel(new Point(3, 1)));
        }

        @Test
        void movesCurrentStateToRedoStack() {
            var original = new Canvas(5, 4);
            history.saveState(original);

            var modified = new Canvas(5, 4);
            modified.drawLine(new Point(1, 1), new Point(3, 1));

            history.undo(modified);

            assertTrue(history.canRedo());
        }

        @Test
        void supportsMultipleUndos() {
            history.saveState(null);

            var canvas1 = new Canvas(5, 4);
            history.saveState(canvas1);

            var canvas2 = new Canvas(5, 4);
            canvas2.drawLine(new Point(1, 1), new Point(3, 1));

            history.undo(canvas2);
            history.undo(canvas1);

            assertFalse(history.canUndo());
        }
    }

    @Nested
    class Redo {
        @Test
        void throwsWhenNothingToRedo() {
            var canvas = new Canvas(5, 4);

            var ex = assertThrows(DrawingException.class,
                    () -> history.redo(canvas));
            assertTrue(ex.getMessage().contains("redo"));
        }

        @Test
        void restoresUndoneState() {
            var original = new Canvas(5, 4);
            history.saveState(original);

            var modified = new Canvas(5, 4);
            modified.drawLine(new Point(1, 1), new Point(3, 1));

            history.undo(modified);
            var result = history.redo(original);

            assertNotNull(result);
            var restored = result.restore();
            assertEquals(LINE_CHAR, restored.getPixel(new Point(1, 1)));
        }

        @Test
        void supportsMultipleRedos() {
            history.saveState(null);
            var canvas1 = new Canvas(5, 4);
            history.saveState(canvas1);

            var canvas2 = new Canvas(5, 4);
            canvas2.drawLine(new Point(1, 1), new Point(3, 1));

            history.undo(canvas2);
            history.undo(canvas1);

            history.redo(null);
            history.redo(canvas1);

            assertFalse(history.canRedo());
        }
    }

    @Nested
    class UndoRedoInteraction {
        @Test
        void undoThenRedoThenNewCommandClearsRedo() {
            var canvas = new Canvas(5, 4);
            history.saveState(null);
            history.undo(canvas);
            assertTrue(history.canRedo());

            history.saveState(canvas);

            assertFalse(history.canRedo());
        }
    }

    @Nested
    class HistoryLimit {
        @Test
        void limitsUndoStackSize() {
            var canvas = new Canvas(5, 4);

            for (var i = 0; i < 60; i++) {
                history.saveState(canvas);
            }

            var undoCount = 0;
            while (history.canUndo()) {
                history.undo(canvas);
                undoCount++;
            }

            assertEquals(50, undoCount);
        }
    }

    @Nested
    class DiscardLastState {
        @Test
        void discardsLastSavedState() {
            var canvas = new Canvas(5, 4);
            history.saveState(canvas);
            assertTrue(history.canUndo());

            history.discardLastState();

            assertFalse(history.canUndo());
        }

        @Test
        void discardsOnlyLastState() {
            var canvas1 = new Canvas(5, 4);
            var canvas2 = new Canvas(5, 4);
            canvas2.drawLine(new Point(1, 1), new Point(3, 1));

            history.saveState(canvas1);
            history.saveState(canvas2);

            history.discardLastState();

            assertTrue(history.canUndo());
            CanvasMemento restored = history.undo(canvas2);
            assertNotNull(restored);
        }

        @Test
        void doesNothingWhenStackIsEmpty() {
            assertFalse(history.canUndo());

            assertDoesNotThrow(() -> history.discardLastState());

            assertFalse(history.canUndo());
        }

        @Test
        void usedForCommandFailureRollback() {
            var original = new Canvas(5, 4);
            original.drawLine(new Point(1, 1), new Point(2, 1));

            history.saveState(original);
            assertTrue(history.canUndo());

            history.discardLastState();

            assertFalse(history.canUndo());
        }
    }
}
