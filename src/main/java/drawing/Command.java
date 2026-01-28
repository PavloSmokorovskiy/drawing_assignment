package drawing;

public interface Command {
    void execute(DrawingContext context);

    default boolean shouldQuit() {
        return false;
    }
}
