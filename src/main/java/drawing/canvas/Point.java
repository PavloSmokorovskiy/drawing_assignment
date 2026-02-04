package drawing.canvas;

/**
 * Неизменяемая 2D-точка (координаты x, y).
 *
 * ПОЧЕМУ RECORD?
 *
 * Point — это "Value Object": два Point с одинаковыми x и y — равны.
 * Record (Java 16+) идеально подходит:
 *
 * - Иммутабельность гарантирована компилятором
 * - equals() и hashCode() генерируются автоматически и корректно
 * - Минимум кода: одна строка вместо 30+ строк boilerplate
 *
 * ВАЖНО ДЛЯ HASHSET В BFS
 *
 * В Canvas.fill() я использую HashSet<Point> для отслеживания посещённых пикселей.
 * Чтобы это работало, Point должен иметь корректные equals() и hashCode().
 *
 * Record генерирует их автоматически на основе всех полей.
 * Если бы это был обычный класс без переопределения — HashSet не работал бы!
 *
 *   Point p1 = new Point(1, 1);
 *   Point p2 = new Point(1, 1);
 *   set.contains(p2)  // false без equals/hashCode, true с ними
 *
 * МЕТОДЫ ПЕРЕМЕЩЕНИЯ
 *
 * moveX() и moveY() возвращают НОВЫЙ Point, а не модифицируют текущий.
 * Это функциональный стиль, безопасный для использования с HashSet.
 */
public record Point(int x, int y) {

    /**
     * Возвращает новую точку, сдвинутую по X.
     *
     * Оригинальная точка не меняется (иммутабельность).
     */
    public Point moveX(int dx) {
        return new Point(x + dx, y);
    }

    /**
     * Возвращает новую точку, сдвинутую по Y.
     */
    public Point moveY(int dy) {
        return new Point(x, y + dy);
    }
}
