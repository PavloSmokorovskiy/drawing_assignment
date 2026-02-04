package drawing.io;

import java.io.PrintStream;

/**
 * Продакшен-реализация Console — пишет в System.out/err.
 *
 * РАЗДЕЛЕНИЕ stdout И stderr
 *
 * В Unix-мире это важно:
 * - stdout для данных (можно перенаправить в файл: app > output.txt)
 * - stderr для ошибок (видно даже при перенаправлении)
 *
 * Поэтому обычный вывод идёт в `out`, а критические ошибки — в `err`.
 *
 * FLUSH ПОСЛЕ print()
 *
 * Обратите внимание на out.flush() в print(). Без этого промпт
 * "enter command: " может не появиться до ввода пользователя,
 * потому что PrintStream буферизирует вывод до \n.
 *
 * flush() гарантирует, что текст появится сразу.
 */
public final class SystemConsole implements Console {

    private final PrintStream out;
    private final PrintStream err;

    /**
     * Стандартный конструктор — System.out и System.err.
     */
    public SystemConsole() {
        this(System.out, System.err);
    }

    /**
     * Конструктор для тестов — можно передать кастомные потоки.
     */
    public SystemConsole(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    @Override
    public void print(String message) {
        out.print(message);
        out.flush();  // Важно! Иначе промпт не появится до ввода
    }

    @Override
    public void println(String message) {
        out.println(message);
    }

    @Override
    public void printError(String message) {
        err.println(message);
    }
}
