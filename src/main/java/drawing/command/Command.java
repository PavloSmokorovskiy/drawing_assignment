package drawing.command;

import drawing.context.DrawingContext;

/**
 * Pattern: Command Pattern (GoF). Sealed interface for exhaustive switch.
 */
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
