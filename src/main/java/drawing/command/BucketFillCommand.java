package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Point;
import drawing.exception.DrawingException;

import static drawing.canvas.DrawingConstants.LINE_CHAR;

/** Delegates to Canvas.fill() (BFS algorithm). Validates reserved color. */
public record BucketFillCommand(Point point, char color) implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        if (color == LINE_CHAR) {
            throw new DrawingException("Cannot use '" + LINE_CHAR + "' as fill color (reserved for lines)");
        }
        var canvas = ctx.requireCanvas();
        canvas.validateBounds(point);
        canvas.fill(point, color);
    }
}
