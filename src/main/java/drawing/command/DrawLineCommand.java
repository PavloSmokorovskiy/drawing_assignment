package drawing.command;

import drawing.context.DrawingContext;
import drawing.canvas.Point;
import drawing.exception.DrawingException;

/**
 * Команда рисования линии.
 *
 * ОГРАНИЧЕНИЕ: только горизонтальные и вертикальные линии.
 *
 * Почему не диагональные? Это требование из задания. Но даже если бы не было —
 * алгоритм Брезенхэма для диагоналей сложнее, а для учебного проекта это overkill.
 *
 * Обратите внимание на порядок валидации в execute():
 * 1. Сначала requireCanvas() — проверяем, что холст создан
 * 2. Затем validateBounds() — точки внутри холста
 * 3. Наконец isDiagonal() — линия горизонтальная/вертикальная
 *
 * Это fail-fast: выходим при первой же проблеме с понятным сообщением.
 *
 * Сама отрисовка делегируется в Canvas.drawLine() — команда не знает,
 * КАК рисовать, она знает только ЧТО нужно сделать.
 */
public record DrawLineCommand(Point from, Point to) implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        var canvas = ctx.requireCanvas();
        canvas.validateBounds(from, to);

        if (isDiagonal()) {
            throw new DrawingException("Only horizontal/vertical lines supported");
        }

        canvas.drawLine(from, to);
    }

    /**
     * Проверяем, диагональная ли линия.
     *
     * Диагональная = и X, и Y отличаются.
     * Горизонтальная = одинаковый Y (from.y == to.y)
     * Вертикальная = одинаковый X (from.x == to.x)
     */
    private boolean isDiagonal() {
        return from.x() != to.x() && from.y() != to.y();
    }
}
