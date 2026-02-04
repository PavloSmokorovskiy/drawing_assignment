package drawing.command;

import drawing.context.DrawingContext;

/**
 * Команда вывода справки.
 *
 * Показывает список всех доступных команд с синтаксисом.
 *
 * TEXT BLOCK (JAVA 15+)
 *
 * Обратите внимание на синтаксис """ для многострочного текста.
 * До Java 15 пришлось бы писать:
 *
 *   "Commands:\n" +
 *   "  C w h           Create canvas\n" +
 *   "  L x1 y1 x2 y2   Draw line\n" +
 *   ...
 *
 * Text block автоматически:
 * - Сохраняет форматирование и отступы
 * - Не требует экранирования \n
 * - Читается как обычный текст
 *
 * Это современная Java-фича, которая значительно улучшает читаемость.
 */
public record HelpCommand() implements Command {

    private static final String HELP_TEXT = """
            Commands:
              C w h           Create canvas (width x height)
              L x1 y1 x2 y2   Draw line (horizontal or vertical)
              R x1 y1 x2 y2   Draw rectangle
              B x y c         Bucket fill at (x,y) with color c
              U               Undo last action
              Z               Redo last undone action
              S <file>        Save canvas to file
              H               Show this help
              Q               Quit
            """;

    @Override
    public void execute(DrawingContext context) {
        context.getConsole().print(HELP_TEXT);
    }

    @Override
    public boolean modifiesCanvas() {
        return false;  // Только вывод текста
    }
}
