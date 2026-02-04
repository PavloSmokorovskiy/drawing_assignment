package drawing;

import drawing.context.DrawingContext;
import drawing.exception.DrawingException;
import drawing.io.Console;
import drawing.io.SystemConsole;
import drawing.parser.CommandParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Это точка входа в приложение и главный оркестратор.
 *
 * Здесь я реализовал классический паттерн REPL (Read-Eval-Print Loop) — бесконечный цикл,
 * который читает команду, выполняет её и выводит результат. Такой же подход используется
 * в интерпретаторах Python, Node.js REPL, командных оболочках типа bash.
 *
 * Почему я вынес это в отдельный класс, а не оставил всё в main()?
 * Потому что так я могу легко тестировать приложение — создаю экземпляр DrawingApp
 * с тестовым Scanner и тестовой Console, и проверяю поведение без реального ввода-вывода.
 *
 * Обратите внимание на конструктор с Console — это Dependency Injection. Вместо того чтобы
 * напрямую писать в System.out, я инжектирую абстракцию. В продакшене это SystemConsole,
 * в тестах — TestConsole, который просто собирает вывод в StringBuilder.
 */
public final class DrawingApp {

    private final Scanner scanner;
    private final boolean interactive;
    private final Console console;
    private final CommandParser parser = new CommandParser();
    private final DrawingContext context;

    /*
     * Два конструктора — это телескопический паттерн.
     * Первый — для продакшена, второй — для тестов с кастомной Console.
     * Можно было бы использовать Builder, но для двух параметров это overkill.
     */

    public DrawingApp(Scanner scanner, boolean interactive) {
        this(scanner, interactive, new SystemConsole());
    }

    public DrawingApp(Scanner scanner, boolean interactive, Console console) {
        this.scanner = scanner;
        this.interactive = interactive;
        this.console = console;
        this.context = new DrawingContext(console);
    }

    /**
     * Главный цикл приложения. Здесь происходит вся магия.
     *
     * Я хочу обратить внимание на несколько важных моментов:
     *
     * 1) ТРАНЗАКЦИОННОСТЬ UNDO
     *    Смотрите на строки 110-121. Перед выполнением команды я сохраняю состояние
     *    в историю (saveState). Но что если команда упадёт с ошибкой? Например,
     *    пользователь пытается нарисовать линию за пределами холста. Я уже сохранил
     *    состояние, но команда не выполнилась — если оставить так, undo будет сломан.
     *
     *    Поэтому в catch-блоке я вызываю discardLastState() — откатываю сохранение.
     *    Это как транзакция в базе данных: либо всё прошло успешно, либо откатываемся.
     *
     * 2) РАЗДЕЛЕНИЕ ОТВЕТСТВЕННОСТИ
     *    Обратите внимание, что run() не знает, как парсить команды — это делает Parser.
     *    Не знает, как рисовать — это делает Canvas внутри Command.
     *    Не знает, как выводить — это делает Console.
     *    Этот класс только координирует взаимодействие между компонентами.
     *
     * 3) GRACEFUL ERROR HANDLING
     *    Все DrawingException ловятся и выводятся пользователю, но приложение
     *    продолжает работать. Это важно для интерактивного режима — одна ошибка
     *    не должна убивать всю сессию.
     */
    public void run() {
        while (true) {
            // В интерактивном режиме показываем промпт, в batch-режиме — нет
            if (interactive) {
                console.print("enter command: ");
            }

            // Если ввод закончился (Ctrl+D или конец файла) — выходим
            if (!scanner.hasNextLine()) {
                break;
            }

            try {
                var line = scanner.nextLine();

                // Пустые строки просто пропускаем — удобно для читаемости файлов с командами
                if (line.isBlank()) {
                    continue;
                }

                // Parser превращает строку в объект Command — это паттерн Command
                var command = parser.parse(line);

                // Quit обрабатываем особо — выходим из метода, не из цикла
                if (command.shouldQuit()) {
                    return;
                }

                /*
                 * Вот ключевой момент для undo/redo!
                 *
                 * Не все команды меняют холст. Help просто выводит текст,
                 * Save пишет в файл, Quit завершает программу.
                 * Для них сохранять состояние бессмысленно.
                 *
                 * А вот для DrawLine, CreateCanvas, BucketFill — нужно.
                 * Метод modifiesCanvas() говорит, нужно ли сохранение.
                 */
                if (command.modifiesCanvas()) {
                    context.getHistory().saveState(context.getCanvas());
                }

                /*
                 * Вложенный try-catch — это та самая транзакционность.
                 * Если execute() бросит исключение, мы откатим сохранённое состояние,
                 * а потом пробросим исключение дальше для вывода пользователю.
                 */
                try {
                    command.execute(context);
                } catch (DrawingException e) {
                    if (command.modifiesCanvas()) {
                        context.getHistory().discardLastState();
                    }
                    throw e;
                }

                // После успешного выполнения показываем холст (если он существует)
                if (context.getCanvas() != null) {
                    console.print(context.getRenderer().render(context.getCanvas()));
                }

            } catch (DrawingException e) {
                // Все ошибки приложения показываем пользователю и продолжаем работу
                console.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Точка входа в программу.
     *
     * Я поддерживаю два режима запуска:
     * - Без аргументов: интерактивный режим, читаем из stdin
     * - С одним аргументом: batch-режим, читаем команды из файла
     *
     * Почему try-with-resources? Потому что InputStream и Scanner — это ресурсы,
     * которые нужно закрывать. Java 7+ позволяет делать это автоматически.
     *
     * Обратите внимание: IOException при старте — это фатальная ошибка (System.exit),
     * а DrawingException во время работы — нет. Разная критичность — разная обработка.
     */
    public static void main(String[] args) {
        var console = new SystemConsole();
        try {
            var source = resolveInput(args);
            try (var stream = source.stream(); var scanner = new Scanner(stream)) {
                new DrawingApp(scanner, source.interactive(), console).run();
            }
        } catch (IOException e) {
            console.printError("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Определяем источник ввода на основе аргументов командной строки.
     *
     * Я вынес это в отдельный метод по двум причинам:
     * 1. main() остаётся чистым и читаемым
     * 2. Логика выбора источника изолирована и легко расширяется
     *    (например, можно добавить чтение из URL или stdin с флагом -i)
     */
    private static InputSource resolveInput(String[] args) throws IOException {
        if (args.length == 0) {
            return new InputSource(System.in, true);
        }
        if (args.length == 1) {
            var path = Path.of(args[0]);
            if (!Files.exists(path)) {
                throw new IOException("File not found: " + path);
            }
            return new InputSource(Files.newInputStream(path), false);
        }
        throw new IOException("Usage: drawing [input-file]");
    }

    /**
     * Простой контейнер для пары "поток + режим".
     *
     * Почему record, а не обычный класс? Потому что это просто данные:
     * - Иммутабельность из коробки
     * - equals/hashCode/toString автоматически
     * - Минимум кода
     *
     * Record появился в Java 16 и идеально подходит для таких DTO.
     */
    private record InputSource(InputStream stream, boolean interactive) {
    }
}
