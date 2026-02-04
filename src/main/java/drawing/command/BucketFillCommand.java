package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Point;
import drawing.exception.DrawingException;

import static drawing.canvas.DrawingConstants.LINE_CHAR;

/**
 * Команда заливки области (Bucket Fill).
 *
 * Работает как "ведро с краской" в графических редакторах:
 * заливает все связанные пиксели одного цвета новым цветом.
 *
 * ОГРАНИЧЕНИЕ: НЕЛЬЗЯ ИСПОЛЬЗОВАТЬ 'x' КАК ЦВЕТ
 *
 * Символ 'x' зарезервирован для линий (LINE_CHAR). Если бы пользователь
 * мог залить область символом 'x', это выглядело бы как линия,
 * что сбивало бы с толку.
 *
 * Это бизнес-правило, поэтому проверка здесь, в команде, а не в Canvas.
 * Canvas — низкоуровневый компонент, он не знает про такие правила.
 *
 * EDGE CASE: ЗАЛИВКА ОБЛАСТИ С ЛИНИЯМИ
 *
 * Если в области есть нарисованные линии, заливка корректно их обходит.
 * Это работает автоматически благодаря алгоритму в Canvas.fill():
 * - Мы ищем пиксели с "целевым" цветом (цвет стартовой точки)
 * - Линии имеют другой цвет ('x'), поэтому не заливаются
 * - Линии становятся естественными границами для заливки
 */
public record BucketFillCommand(Point point, char color) implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        // Бизнес-правило: 'x' нельзя использовать как цвет заливки
        if (color == LINE_CHAR) {
            throw new DrawingException("Cannot use '" + LINE_CHAR + "' as fill color (reserved for lines)");
        }

        var canvas = ctx.requireCanvas();
        canvas.validateBounds(point);
        canvas.fill(point, color);
    }
}
