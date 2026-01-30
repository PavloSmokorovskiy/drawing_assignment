package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.CanvasMemento;

public record UndoCommand() implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        var previousState = ctx.getHistory().undo(ctx.getCanvas());
        var restoredCanvas = previousState == null ? null : previousState.restore();
        ctx.setCanvas(restoredCanvas);
    }

    @Override
    public boolean modifiesCanvas() {
        return false;
    }
}
