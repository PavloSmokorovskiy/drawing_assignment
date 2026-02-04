package drawing.command;

import drawing.context.DrawingContext;
import drawing.exception.DrawingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Команда сохранения холста в файл.
 *
 * Сохраняет отрендеренный вид холста (с рамками) как текстовый файл.
 * Выходной файл выглядит точно так же, как вывод в консоль.
 *
 * ПОЧЕМУ Files.writeString(), А НЕ FileWriter/BufferedWriter?
 *
 * Files.writeString() (Java 11+) — это современный API:
 * - Одна строка вместо try-finally с ресурсами
 * - Автоматическое управление ресурсами
 * - Чистый и понятный код
 *
 * Для маленьких текстовых файлов (а холст — это максимум 1000 строк)
 * это оптимальный выбор.
 *
 * ПОЧЕМУ modifiesCanvas() ВОЗВРАЩАЕТ false?
 *
 * Save не меняет холст — только читает и пишет в файл.
 * Не нужно сохранять состояние для undo после записи файла.
 */
public record SaveCommand(String filename) implements Command {

    @Override
    public void execute(DrawingContext context) {
        var canvas = context.requireCanvas();

        // Используем тот же renderer, что и для вывода в консоль
        var content = context.getRenderer().render(canvas);

        try {
            Files.writeString(Path.of(filename), content);
            context.getConsole().println("Canvas saved to: " + filename);
        } catch (IOException e) {
            // Оборачиваем IOException в DrawingException для единообразной обработки
            throw new DrawingException("Failed to save: " + e.getMessage());
        }
    }

    @Override
    public boolean modifiesCanvas() {
        return false;  // Файловая операция, холст не меняется
    }
}
