package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Canvas;
import drawing.canvas.CanvasMemento;

public record RedoCommand() implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        CanvasMemento nextState = ctx.getHistory().redo(ctx.getCanvas());
        Canvas restoredCanvas = nextState == null ? null : nextState.restore();
        ctx.setCanvas(restoredCanvas);
    }

    @Override
    public boolean modifiesCanvas() {
        return false;
    }
}
