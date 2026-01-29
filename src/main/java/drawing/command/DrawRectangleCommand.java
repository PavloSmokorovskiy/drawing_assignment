package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Canvas;
import drawing.canvas.Point;

public record DrawRectangleCommand(Point corner1, Point corner2) implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        Canvas canvas = ctx.requireCanvas();
        canvas.validateBounds(corner1, corner2);

        Point topLeft = new Point(Math.min(corner1.x(), corner2.x()), Math.min(corner1.y(), corner2.y()));
        Point bottomRight = new Point(Math.max(corner1.x(), corner2.x()), Math.max(corner1.y(), corner2.y()));
        Point topRight = new Point(bottomRight.x(), topLeft.y());
        Point bottomLeft = new Point(topLeft.x(), bottomRight.y());

        canvas.drawLine(topLeft, topRight);
        canvas.drawLine(bottomLeft, bottomRight);
        canvas.drawLine(topLeft, bottomLeft);
        canvas.drawLine(topRight, bottomRight);
    }
}
