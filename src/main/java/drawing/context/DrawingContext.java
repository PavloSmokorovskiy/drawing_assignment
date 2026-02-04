package drawing.context;

import drawing.canvas.Canvas;
import drawing.canvas.CanvasRenderer;
import drawing.exception.DrawingException;
import drawing.history.CommandHistory;
import drawing.io.Console;
import drawing.io.SystemConsole;

/**
 * Контейнер для всего состояния приложения.
 *
 * ПАТТЕРН CONTEXT OBJECT
 *
 * Вместо того чтобы передавать в Command.execute() кучу параметров:
 *
 *   void execute(Canvas canvas, CommandHistory history, Console console, CanvasRenderer renderer);
 *
 * Мы передаём один объект-контейнер:
 *
 *   void execute(DrawingContext context);
 *
 * Преимущества:
 * - Чистая сигнатура методов
 * - Легко добавлять новые зависимости (не меняем интерфейс)
 * - Команды берут только то, что им нужно
 *
 * DEPENDENCY INJECTION
 *
 * Обратите внимание на конструктор с Console. Это позволяет:
 * - В продакшене: new DrawingContext() — используется SystemConsole
 * - В тестах: new DrawingContext(testConsole) — используется TestConsole
 *
 * Так мы тестируем вывод без мокирования System.out.
 *
 * ИЗМЕНЯЕМОЕ vs НЕИЗМЕНЯЕМОЕ
 *
 * - Canvas — изменяемый (setCanvas), потому что меняется при CreateCanvas, Undo, Redo
 * - Остальное — неизменяемое (final), создаётся один раз при старте
 */
public final class DrawingContext {

    private Canvas canvas;  // Может быть null до первого C-команды
    private final CommandHistory history = new CommandHistory();
    private final CanvasRenderer renderer = new CanvasRenderer();
    private final Console console;

    /**
     * Конструктор для продакшена — используется SystemConsole.
     */
    public DrawingContext() {
        this(new SystemConsole());
    }

    /**
     * Конструктор для тестов — можно передать TestConsole.
     */
    public DrawingContext(Console console) {
        this.console = console;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public CommandHistory getHistory() {
        return history;
    }

    public CanvasRenderer getRenderer() {
        return renderer;
    }

    public Console getConsole() {
        return console;
    }

    /**
     * Возвращает холст или бросает исключение, если холст не создан.
     *
     * Это удобный метод для команд, которым нужен холст.
     * Вместо проверки if (canvas == null) в каждой команде,
     * они просто вызывают ctx.requireCanvas() и получают:
     * - Либо холст
     * - Либо понятное исключение для пользователя
     *
     * Это fail-fast подход: падаем сразу с понятным сообщением.
     */
    public Canvas requireCanvas() {
        if (canvas == null) {
            throw new DrawingException("Canvas not created. Use: C <width> <height>");
        }
        return canvas;
    }
}
