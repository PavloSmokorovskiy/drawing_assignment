package drawing.command;

import drawing.context.DrawingContext;

public record QuitCommand() implements Command {

    @Override
    public void execute(DrawingContext ctx) {
    }

    @Override
    public boolean shouldQuit() {
        return true;
    }

    @Override
    public boolean modifiesCanvas() {
        return false;
    }
}
