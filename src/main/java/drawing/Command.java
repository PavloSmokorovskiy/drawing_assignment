package drawing;

public sealed interface Command permits CreateCanvasCommand, DrawLineCommand, DrawRectangleCommand, BucketFillCommand, QuitCommand, UndoCommand, RedoCommand {

    void execute(DrawingContext context);

    default boolean shouldQuit() {
        return false;
    }

    default boolean modifiesCanvas() {
        return true;
    }
}
