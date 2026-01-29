package drawing.command;

import drawing.context.DrawingContext;

public sealed interface Command permits CreateCanvasCommand, DrawLineCommand, DrawRectangleCommand, BucketFillCommand,
        QuitCommand, UndoCommand, RedoCommand, HelpCommand, SaveCommand {

    void execute(DrawingContext context);

    default boolean shouldQuit() {
        return false;
    }

    default boolean modifiesCanvas() {
        return true;
    }
}
