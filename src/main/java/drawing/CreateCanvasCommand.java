package drawing;

public record CreateCanvasCommand(int width, int height) implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        ctx.setCanvas(new Canvas(width, height));
    }
}
