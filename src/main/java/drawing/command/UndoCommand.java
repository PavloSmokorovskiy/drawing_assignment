package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Canvas;
import drawing.canvas.CanvasMemento;

import java.util.Optional;

public record UndoCommand() implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        Optional<CanvasMemento> previousState = ctx.getHistory().undo(ctx.getCanvas());
        Canvas restoredCanvas = previousState.map(CanvasMemento::restore).orElse(null);
        ctx.setCanvas(restoredCanvas);
    }

    @Override
    public boolean modifiesCanvas() {
        return false;
    }
}
