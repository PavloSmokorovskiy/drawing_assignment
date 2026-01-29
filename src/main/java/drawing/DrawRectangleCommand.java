package drawing;

public record DrawRectangleCommand(Point corner1, Point corner2) implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        ctx.requireCanvas().validateBounds(corner1, corner2);

        Point topLeft = new Point(Math.min(corner1.x(), corner2.x()), Math.min(corner1.y(), corner2.y()));
        Point bottomRight = new Point(Math.max(corner1.x(), corner2.x()), Math.max(corner1.y(), corner2.y()));
        Point topRight = new Point(bottomRight.x(), topLeft.y());
        Point bottomLeft = new Point(topLeft.x(), bottomRight.y());

        new DrawLineCommand(topLeft, topRight).execute(ctx);
        new DrawLineCommand(bottomLeft, bottomRight).execute(ctx);
        new DrawLineCommand(topLeft, bottomLeft).execute(ctx);
        new DrawLineCommand(topRight, bottomRight).execute(ctx);
    }
}
