package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Canvas;
import drawing.exception.DrawingException;

import static drawing.canvas.DrawingConstants.MAX_CANVAS_HEIGHT;
import static drawing.canvas.DrawingConstants.MAX_CANVAS_WIDTH;

/**
 * Команда создания нового холста.
 *
 * Это первая команда, которую пользователь должен выполнить.
 * Без холста другие команды (DrawLine, BucketFill) не работают.
 *
 * ОГРАНИЧЕНИЕ РАЗМЕРА
 *
 * Я ограничил максимальный размер до 1000x1000. Почему?
 *
 * 1) Память: char[][] размером 10000x10000 = 100 миллионов символов = ~200MB
 *    Это слишком много для консольного приложения.
 *
 * 2) Производительность: BucketFill на таком холсте будет работать секунды.
 *
 * 3) UX: в консоли холст 1000x1000 уже невозможно нормально отобразить.
 *
 * Почему проверка в execute(), а не в конструкторе?
 * Потому что константы MAX_* — это конфигурация приложения.
 * Parser создаёт команду с любыми числами, а execute() проверяет
 * по правилам приложения. Это разделение ответственности.
 */
public record CreateCanvasCommand(int width, int height) implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        if (width > MAX_CANVAS_WIDTH || height > MAX_CANVAS_HEIGHT) {
            throw new DrawingException(
                    "Canvas size exceeds maximum allowed (%dx%d)".formatted(MAX_CANVAS_WIDTH, MAX_CANVAS_HEIGHT));
        }
        ctx.setCanvas(new Canvas(width, height));
    }
}
