package drawing.parser;

import drawing.canvas.Point;
import drawing.command.BucketFillCommand;
import drawing.command.Command;
import drawing.command.CreateCanvasCommand;
import drawing.command.DrawLineCommand;
import drawing.command.DrawRectangleCommand;
import drawing.command.HelpCommand;
import drawing.command.QuitCommand;
import drawing.command.RedoCommand;
import drawing.command.SaveCommand;
import drawing.command.UndoCommand;
import drawing.exception.DrawingException;

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
            case "S" -> parseSave(parts);
            case "U" -> new UndoCommand();
            case "Z" -> new RedoCommand();
            case "H" -> new HelpCommand();
            case "Q" -> new QuitCommand();
            default -> throw new DrawingException("Unknown command: " + type + ". Type H for help");
        };
    }

    private Command parseCanvas(String[] p) {
        require(p, 3, "C <width> <height>");
        return new CreateCanvasCommand(toInt(p[1], "width"), toInt(p[2], "height"));
    }

    private Command parseLine(String[] p) {
        require(p, 5, "L <x1> <y1> <x2> <y2>");
        return new DrawLineCommand(new Point(toInt(p[1], "x1"), toInt(p[2], "y1")), new Point(toInt(p[3], "x2"), toInt(p[4], "y2")));
    }

    private Command parseRectangle(String[] p) {
        require(p, 5, "R <x1> <y1> <x2> <y2>");
        return new DrawRectangleCommand(new Point(toInt(p[1], "x1"), toInt(p[2], "y1")), new Point(toInt(p[3], "x2"), toInt(p[4], "y2")));
    }

    private Command parseFill(String[] p) {
        require(p, 4, "B <x> <y> <color>");
        if (p[3].length() != 1) {
            throw new DrawingException("Color must be a single character");
        }
        char color = p[3].charAt(0);
        return new BucketFillCommand(new Point(toInt(p[1], "x"), toInt(p[2], "y")), color);
    }

    private Command parseSave(String[] p) {
        require(p, 2, "S <filename>");
        return new SaveCommand(p[1]);
    }

    private void require(String[] parts, int n, String usage) {
        if (parts.length != n) {
            throw new DrawingException("Usage: " + usage);
        }
    }

    private int toInt(String s, String name) {
        try {
            int val = Integer.parseInt(s);
            if (val <= 0) {
                throw new DrawingException(name + " must be positive");
            }
            return val;
        } catch (NumberFormatException e) {
            throw new DrawingException(name + " must be a number");
        }
    }
}
