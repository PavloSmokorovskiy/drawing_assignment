package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Point;

/**
 * Команда рисования прямоугольника.
 *
 * РЕАЛИЗАЦИЯ ЧЕРЕЗ КОМПОЗИЦИЮ
 *
 * Прямоугольник — это просто 4 линии. Вместо того чтобы писать отдельный
 * алгоритм в Canvas, я переиспользую drawLine().
 *
 * Преимущества:
 * - DRY: один алгоритм отрисовки линии
 * - Консистентность: линии прямоугольника выглядят так же, как обычные линии
 * - Простота: меньше кода = меньше багов
 *
 * НОРМАЛИЗАЦИЯ УГЛОВ
 *
 * Пользователь может указать любые два противоположных угла в любом порядке:
 * - R 1 1 5 5  (top-left, bottom-right)
 * - R 5 5 1 1  (bottom-right, top-left)
 * - R 1 5 5 1  (bottom-left, top-right)
 * - R 5 1 1 5  (top-right, bottom-left)
 *
 * Все варианты дают одинаковый прямоугольник. Я вычисляю реальные углы
 * через Math.min/max — так пользователю не нужно думать о порядке.
 */
public record DrawRectangleCommand(Point corner1, Point corner2) implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        var canvas = ctx.requireCanvas();
        canvas.validateBounds(corner1, corner2);

        // Вычисляем все 4 угла из двух противоположных
        var topLeft = new Point(Math.min(corner1.x(), corner2.x()), Math.min(corner1.y(), corner2.y()));
        var bottomRight = new Point(Math.max(corner1.x(), corner2.x()), Math.max(corner1.y(), corner2.y()));
        var topRight = new Point(bottomRight.x(), topLeft.y());
        var bottomLeft = new Point(topLeft.x(), bottomRight.y());

        // Рисуем 4 стороны
        canvas.drawLine(topLeft, topRight);      // верхняя
        canvas.drawLine(bottomLeft, bottomRight); // нижняя
        canvas.drawLine(topLeft, bottomLeft);     // левая
        canvas.drawLine(topRight, bottomRight);   // правая
    }
}
