package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Canvas;
import drawing.canvas.CanvasMemento;

public record UndoCommand() implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        CanvasMemento previousState = ctx.getHistory().undo(ctx.getCanvas());
        Canvas restoredCanvas = previousState == null ? null : previousState.restore();
        ctx.setCanvas(restoredCanvas);
    }

    @Override
    public boolean modifiesCanvas() {
        return false;
    }
}
