package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Canvas;
import drawing.exception.DrawingException;

import static drawing.canvas.DrawingConstants.MAX_CANVAS_HEIGHT;
import static drawing.canvas.DrawingConstants.MAX_CANVAS_WIDTH;

/** Creates new canvas, replacing existing. Validates max dimensions. */
public record CreateCanvasCommand(int width, int height) implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        if (width > MAX_CANVAS_WIDTH || height > MAX_CANVAS_HEIGHT) {
            throw new DrawingException(
                    "Canvas size exceeds maximum allowed (%dx%d)".formatted(MAX_CANVAS_WIDTH, MAX_CANVAS_HEIGHT));
        }
        ctx.setCanvas(new Canvas(width, height));
    }
}
