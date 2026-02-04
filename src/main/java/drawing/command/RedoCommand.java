package drawing.command;

import drawing.context.DrawingContext;

/** Restores undone state via Memento. modifiesCanvas=false (history manages state). */
public record RedoCommand() implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        var nextState = ctx.getHistory().redo(ctx.getCanvas());
        var restoredCanvas = nextState == null ? null : nextState.restore();
        ctx.setCanvas(restoredCanvas);
    }

    @Override
    public boolean modifiesCanvas() {
        return false;
    }
}
