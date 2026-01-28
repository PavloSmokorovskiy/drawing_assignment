package drawing;

public record DrawLineCommand(Point from, Point to) implements Command {
    @Override
    public void execute(DrawingContext ctx) {
        var canvas = ctx.requireCanvas();
        canvas.validateBounds(from, to);
        if (isDiagonal()) {
            throw new DrawingException("Only horizontal/vertical lines supported");
        }
        canvas.drawLine(from, to);
    }

    private boolean isDiagonal() {
        return from.x() != to.x() && from.y() != to.y();
    }
}
