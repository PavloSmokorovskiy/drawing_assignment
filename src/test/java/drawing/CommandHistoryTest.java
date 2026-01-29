package drawing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static drawing.DrawingConstants.LINE_CHAR;
import static org.junit.jupiter.api.Assertions.*;

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
            Canvas canvas = new Canvas(5, 4);
            history.saveState(canvas);

            assertTrue(history.canUndo());
        }

        @Test
        void clearsRedoStackOnNewSave() {
            Canvas canvas = new Canvas(5, 4);
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
            Canvas canvas = new Canvas(5, 4);

            DrawingException ex = assertThrows(DrawingException.class,
                    () -> history.undo(canvas));
            assertTrue(ex.getMessage().contains("undo"));
        }

        @Test
        void returnsEmptyOptionalForNullState() {
            history.saveState(null);
            Canvas canvas = new Canvas(5, 4);

            Optional<CanvasMemento> result = history.undo(canvas);

            assertTrue(result.isEmpty());
        }

        @Test
        void returnsPreviousState() {
            Canvas original = new Canvas(5, 4);
            original.drawLine(new Point(1, 1), new Point(3, 1));
            history.saveState(original);

            Canvas modified = new Canvas(5, 4);
            modified.drawLine(new Point(1, 1), new Point(5, 1));

            Optional<CanvasMemento> result = history.undo(modified);

            assertTrue(result.isPresent());
            Canvas restored = result.get().restore();
            assertEquals(LINE_CHAR, restored.getPixel(new Point(3, 1)));
        }

        @Test
        void movesCurrentStateToRedoStack() {
            Canvas original = new Canvas(5, 4);
            history.saveState(original);

            Canvas modified = new Canvas(5, 4);
            modified.drawLine(new Point(1, 1), new Point(3, 1));

            history.undo(modified);

            assertTrue(history.canRedo());
        }

        @Test
        void supportsMultipleUndos() {
            history.saveState(null);

            Canvas canvas1 = new Canvas(5, 4);
            history.saveState(canvas1);

            Canvas canvas2 = new Canvas(5, 4);
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
            Canvas canvas = new Canvas(5, 4);

            DrawingException ex = assertThrows(DrawingException.class,
                    () -> history.redo(canvas));
            assertTrue(ex.getMessage().contains("redo"));
        }

        @Test
        void restoresUndoneState() {
            Canvas original = new Canvas(5, 4);
            history.saveState(original);

            Canvas modified = new Canvas(5, 4);
            modified.drawLine(new Point(1, 1), new Point(3, 1));

            history.undo(modified);
            Optional<CanvasMemento> result = history.redo(original);

            assertTrue(result.isPresent());
            Canvas restored = result.get().restore();
            assertEquals(LINE_CHAR, restored.getPixel(new Point(1, 1)));
        }

        @Test
        void supportsMultipleRedos() {
            history.saveState(null);
            Canvas canvas1 = new Canvas(5, 4);
            history.saveState(canvas1);

            Canvas canvas2 = new Canvas(5, 4);
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
            Canvas canvas = new Canvas(5, 4);
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
            Canvas canvas = new Canvas(5, 4);

            for (int i = 0; i < 60; i++) {
                history.saveState(canvas);
            }

            int undoCount = 0;
            while (history.canUndo()) {
                history.undo(canvas);
                undoCount++;
            }

            assertEquals(50, undoCount);
        }
    }
}
