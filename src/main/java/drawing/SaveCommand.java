package drawing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record SaveCommand(String filename) implements Command {

    @Override
    public void execute(DrawingContext context) {
        Canvas canvas = context.requireCanvas();
        CanvasRenderer renderer = new CanvasRenderer();
        String content = renderer.render(canvas);

        try {
            Files.writeString(Path.of(filename), content);
            System.out.println("Canvas saved to: " + filename);
        } catch (IOException e) {
            throw new DrawingException("Failed to save: " + e.getMessage());
        }
    }

    @Override
    public boolean modifiesCanvas() {
        return false;
    }
}
