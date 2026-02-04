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

/**
 * Парсер пользовательского ввода.
 *
 * Превращает строку "L 1 2 5 2" в объект DrawLineCommand(Point(1,2), Point(5,2)).
 *
 * РАЗДЕЛЕНИЕ ОТВЕТСТВЕННОСТИ
 *
 * Parser занимается ТОЛЬКО синтаксисом:
 * - Разбивает строку на части
 * - Определяет тип команды
 * - Парсит аргументы
 * - Проверяет количество аргументов
 *
 * Parser НЕ занимается семантикой:
 * - Не проверяет, существует ли холст
 * - Не проверяет, в границах ли точки
 * - Не проверяет бизнес-правила
 *
 * Семантические проверки делают команды в execute().
 * Это разделение упрощает тестирование и делает код понятнее.
 *
 * SWITCH EXPRESSION (JAVA 14+)
 *
 * Обратите внимание на синтаксис switch с ->. Это:
 * - Короче: не нужен break
 * - Безопаснее: нет fall-through багов
 * - Возвращает значение: switch — это выражение, не statement
 */
public final class CommandParser {

    /**
     * Парсит строку ввода в объект Command.
     *
     * Регистр команды не важен: "L", "l", "L " — всё работает.
     */
    public Command parse(String input) {
        if (input == null || input.isBlank()) {
            throw new DrawingException("Empty command");
        }

        // Разбиваем по пробелам, trim убирает лишние пробелы по краям
        var parts = input.trim().split("\\s+");
        var type = parts[0].toUpperCase();

        // Switch expression — современный синтаксис Java 14+
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

    /**
     * Парсит команду создания холста: C width height
     */
    private Command parseCanvas(String[] p) {
        require(p, 3, "C <width> <height>");
        return new CreateCanvasCommand(toInt(p[1], "width"), toInt(p[2], "height"));
    }

    /**
     * Парсит команду рисования линии: L x1 y1 x2 y2
     */
    private Command parseLine(String[] p) {
        require(p, 5, "L <x1> <y1> <x2> <y2>");
        var from = new Point(toInt(p[1], "x1"), toInt(p[2], "y1"));
        var to = new Point(toInt(p[3], "x2"), toInt(p[4], "y2"));
        return new DrawLineCommand(from, to);
    }

    /**
     * Парсит команду рисования прямоугольника: R x1 y1 x2 y2
     */
    private Command parseRectangle(String[] p) {
        require(p, 5, "R <x1> <y1> <x2> <y2>");
        var corner1 = new Point(toInt(p[1], "x1"), toInt(p[2], "y1"));
        var corner2 = new Point(toInt(p[3], "x2"), toInt(p[4], "y2"));
        return new DrawRectangleCommand(corner1, corner2);
    }

    /**
     * Парсит команду заливки: B x y color
     *
     * Цвет должен быть ровно одним символом.
     */
    private Command parseFill(String[] p) {
        require(p, 4, "B <x> <y> <color>");
        if (p[3].length() != 1) {
            throw new DrawingException("Color must be a single character");
        }
        var color = p[3].charAt(0);
        return new BucketFillCommand(new Point(toInt(p[1], "x"), toInt(p[2], "y")), color);
    }

    /**
     * Парсит команду сохранения: S filename
     */
    private Command parseSave(String[] p) {
        require(p, 2, "S <filename>");
        return new SaveCommand(p[1]);
    }

    /**
     * Проверяет количество аргументов.
     *
     * При ошибке показывает правильный синтаксис — это UX:
     * вместо "wrong number of arguments" пользователь видит
     * "Usage: L <x1> <y1> <x2> <y2>" и сразу понимает, что делать.
     */
    private void require(String[] parts, int n, String usage) {
        if (parts.length != n) {
            throw new DrawingException("Usage: " + usage);
        }
    }

    /**
     * Парсит строку в положительное целое число.
     *
     * Почему положительное? Потому что координаты и размеры не могут быть ≤ 0.
     * Это бизнес-правило, но оно настолько базовое, что проверяем здесь.
     *
     * Сообщения об ошибках включают имя параметра:
     * "width must be positive" понятнее, чем "value must be positive"
     */
    private int toInt(String s, String name) {
        try {
            var val = Integer.parseInt(s);
            if (val <= 0) {
                throw new DrawingException(name + " must be positive");
            }
            return val;
        } catch (NumberFormatException e) {
            throw new DrawingException(name + " must be a number");
        }
    }
}
