package drawing;

public record BucketFillCommand(Point point, char color) implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        var canvas = ctx.requireCanvas();
        canvas.validateBounds(point);
        canvas.fill(point, color);
    }
}
