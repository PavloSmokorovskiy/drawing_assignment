package drawing;

public final class CommandParser {
    public Command parse(String input) {
        if (input == null || input.isBlank()) {
            throw new DrawingException("Empty command");
        }

        var parts = input.trim().split("\\s+");
        var type = parts[0].toUpperCase();

        return switch (type) {
            case "C" -> parseCanvas(parts);
            case "L" -> parseLine(parts);
            case "R" -> parseRectangle(parts);
            case "B" -> parseFill(parts);
            case "Q" -> new QuitCommand();
            default -> throw new DrawingException("Unknown command: " + type);
        };
    }

    private Command parseCanvas(String[] parts) {
        int width = Integer.parseInt(parts[1]);
        int height = Integer.parseInt(parts[2]);
        return new CreateCanvasCommand(width, height);
    }

    private Command parseLine(String[] parts) {
        Point from = new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        Point to = new Point(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
        return new DrawLineCommand(from, to);
    }

    private Command parseRectangle(String[] parts) {
        Point corner1 = new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        Point corner2 = new Point(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
        return new DrawRectangleCommand(corner1, corner2);
    }

    private Command parseFill(String[] parts) {
        Point point = new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        char color = parts[3].charAt(0);
        return new BucketFillCommand(point, color);
    }
}
