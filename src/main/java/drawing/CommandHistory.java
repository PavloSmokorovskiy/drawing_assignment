package drawing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public final class CommandHistory {

    private final Deque<Optional<CanvasMemento>> undoStack = new ArrayDeque<>();
    private final Deque<Optional<CanvasMemento>> redoStack = new ArrayDeque<>();

    public void saveState(Canvas canvas) {
        Optional<CanvasMemento> memento = canvas == null
                ? Optional.empty()
                : Optional.of(CanvasMemento.from(canvas));
        undoStack.push(memento);
        redoStack.clear();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public Optional<CanvasMemento> undo(Canvas currentCanvas) {
        if (!canUndo()) {
            throw new DrawingException("Nothing to undo");
        }

        Optional<CanvasMemento> currentMemento = currentCanvas == null
                ? Optional.empty()
                : Optional.of(CanvasMemento.from(currentCanvas));
        redoStack.push(currentMemento);

        return undoStack.pop();
    }

    public Optional<CanvasMemento> redo(Canvas currentCanvas) {
        if (!canRedo()) {
            throw new DrawingException("Nothing to redo");
        }

        Optional<CanvasMemento> currentMemento = currentCanvas == null
                ? Optional.empty()
                : Optional.of(CanvasMemento.from(currentCanvas));
        undoStack.push(currentMemento);

        return redoStack.pop();
    }
}
