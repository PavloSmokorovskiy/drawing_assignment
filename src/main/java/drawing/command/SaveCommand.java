package drawing.command;

import drawing.context.DrawingContext;
import drawing.exception.DrawingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record SaveCommand(String filename) implements Command {

    @Override
    public void execute(DrawingContext context) {
        var canvas = context.requireCanvas();
        var content = context.getRenderer().render(canvas);

        try {
            Files.writeString(Path.of(filename), content);
            context.getConsole().println("Canvas saved to: " + filename);
        } catch (IOException e) {
            throw new DrawingException("Failed to save: " + e.getMessage());
        }
    }

    @Override
    public boolean modifiesCanvas() {
        return false;
    }
}
