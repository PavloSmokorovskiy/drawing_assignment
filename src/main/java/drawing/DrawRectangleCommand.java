package drawing;

public record DrawRectangleCommand(Point corner1, Point corner2) implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        var canvas = ctx.requireCanvas();
        canvas.validateBounds(corner1, corner2);
        canvas.drawRectangle(corner1, corner2);
    }
}
