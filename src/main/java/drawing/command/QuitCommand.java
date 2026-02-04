package drawing.command;

import drawing.context.DrawingContext;

/** REPL termination. shouldQuit=true signals main loop to exit. */
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
