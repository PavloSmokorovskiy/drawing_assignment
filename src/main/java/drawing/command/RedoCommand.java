package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Canvas;
import drawing.canvas.CanvasMemento;

import java.util.Optional;

public record RedoCommand() implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        Optional<CanvasMemento> nextState = ctx.getHistory().redo(ctx.getCanvas());
        Canvas restoredCanvas = nextState.map(CanvasMemento::restore).orElse(null);
        ctx.setCanvas(restoredCanvas);
    }

    @Override
    public boolean modifiesCanvas() {
        return false;
    }
}
