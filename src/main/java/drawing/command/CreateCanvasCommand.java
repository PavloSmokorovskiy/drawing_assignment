package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Canvas;

public record CreateCanvasCommand(int width, int height) implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        ctx.setCanvas(new Canvas(width, height));
    }
}
