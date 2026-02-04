package drawing.history;

import drawing.canvas.Canvas;
import drawing.canvas.CanvasMemento;
import drawing.exception.DrawingException;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Pattern: Caretaker (Memento). Manages undo/redo stacks.
 * LinkedList used because ArrayDeque doesn't support null elements.
 */
public final class CommandHistory {

    private static final int MAX_HISTORY_SIZE = 50;

    private final Deque<CanvasMemento> undoStack = new LinkedList<>();
    private final Deque<CanvasMemento> redoStack = new LinkedList<>();

    public void saveState(Canvas canvas) {
        var memento = canvas == null ? null : CanvasMemento.from(canvas);
        undoStack.push(memento);

        if (undoStack.size() > MAX_HISTORY_SIZE) {
            undoStack.removeLast();
        }

        redoStack.clear();
    }

    public void discardLastState() {
        if (!undoStack.isEmpty()) {
            undoStack.pop();
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public CanvasMemento undo(Canvas currentCanvas) {
        if (!canUndo()) {
            throw new DrawingException("Nothing to undo");
        }

        var currentMemento = currentCanvas == null ? null : CanvasMemento.from(currentCanvas);
        redoStack.push(currentMemento);

        return undoStack.pop();
    }

    public CanvasMemento redo(Canvas currentCanvas) {
        if (!canRedo()) {
            throw new DrawingException("Nothing to redo");
        }

        var currentMemento = currentCanvas == null ? null : CanvasMemento.from(currentCanvas);
        undoStack.push(currentMemento);

        return redoStack.pop();
    }
}
