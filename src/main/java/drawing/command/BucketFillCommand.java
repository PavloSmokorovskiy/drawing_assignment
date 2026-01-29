package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Point;

public record BucketFillCommand(Point point, char color) implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        var canvas = ctx.requireCanvas();
        canvas.validateBounds(point);
        canvas.fill(point, color);
    }
}
