package drawing.canvas;

import drawing.exception.DrawingException;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static drawing.canvas.DrawingConstants.EMPTY_CHAR;
import static drawing.canvas.DrawingConstants.LINE_CHAR;

/**
 * Это сердце приложения — холст для рисования.
 *
 * По сути это просто двумерный массив символов, но с несколькими важными решениями:
 *
 * 1) КООРДИНАТЫ С ЕДИНИЦЫ, НЕ С НУЛЯ
 *    Пользователь работает с координатами (1,1) - (width, height), а не (0,0) - (w-1, h-1).
 *    Это интуитивнее для людей. Внутри класса я конвертирую: pixels[y-1][x-1].
 *
 * 2) РАЗДЕЛЕНИЕ ДАННЫХ И ПРЕДСТАВЛЕНИЯ
 *    Canvas хранит ТОЛЬКО пиксели — никаких рамок, бордеров.
 *    Рамки добавляет CanvasRenderer при выводе. Это важно, потому что:
 *    - Экономим память (не храним лишние символы)
 *    - Можно легко поменять формат вывода без изменения Canvas
 *    - Координаты пользователя соответствуют индексам массива (после -1)
 *
 * 3) ИММУТАБЕЛЬНЫЕ РАЗМЕРЫ
 *    width и height задаются в конструкторе и не меняются. Если нужен другой размер —
 *    создаём новый Canvas. Это упрощает рассуждение о коде и Memento-паттерн.
 *
 * 4) PACKAGE-PRIVATE ДОСТУП К ПИКСЕЛЯМ
 *    Методы copyPixels() и getPixelRaw() — package-private. Только CanvasRenderer
 *    и CanvasMemento имеют к ним доступ. Внешний код работает через Point-методы.
 */
public final class Canvas {

    private final int width;
    private final int height;
    private final char[][] pixels;

    /**
     * Создаёт пустой холст заданного размера.
     * Все пиксели инициализируются пробелами (EMPTY_CHAR).
     */
    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new char[height][width];
        clear();
    }

    /**
     * Package-private конструктор для восстановления из Memento.
     *
     * Почему он не public? Потому что я не хочу, чтобы внешний код
     * мог создать Canvas с произвольным массивом пикселей.
     * Это нарушило бы инкапсуляцию и могло бы привести к багам.
     */
    Canvas(int width, int height, char[][] pixels) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    /**
     * Создаёт глубокую копию массива пикселей для Memento.
     *
     * Почему глубокая копия? Потому что двумерный массив — это массив ссылок на строки.
     * Если сделать просто pixels.clone(), мы скопируем ссылки, но не сами строки.
     * Изменение оригинала изменит и "копию". Это классическая ловушка в Java.
     *
     * Я вынес логику копирования в PixelArrays, чтобы не дублировать код
     * здесь и в CanvasMemento.
     */
    char[][] copyPixels() {
        return PixelArrays.copy(pixels);
    }

    /**
     * Прямой доступ к пикселю по 0-based индексам.
     *
     * Используется только CanvasRenderer для эффективной итерации.
     * Конвертация координат на каждый пиксель при рендеринге — это лишние операции.
     */
    char getPixelRaw(int x, int y) {
        return pixels[y][x];
    }

    /**
     * Проверка выхода за границы (1-based координаты).
     */
    private boolean isOutOfBounds(Point p) {
        return p.x() < 1 || p.x() > width || p.y() < 1 || p.y() > height;
    }

    /**
     * Проверяем, что все точки внутри холста.
     *
     * Почему varargs? Потому что обычно проверяем 1-2 точки (начало и конец линии).
     * Вызов canvas.validateBounds(from, to) читается лучше, чем
     * canvas.validateBounds(new Point[]{from, to}).
     *
     * При ошибке выбрасываем исключение с понятным сообщением —
     * пользователь сразу видит, какая точка и какой размер холста.
     */
    public void validateBounds(Point... points) {
        for (var p : points) {
            if (isOutOfBounds(p)) {
                throw new DrawingException(
                        "Point (%d,%d) out of bounds (canvas: %dx%d)"
                                .formatted(p.x(), p.y(), width, height));
            }
        }
    }

    /**
     * Получение пикселя по 1-based координатам (для команд).
     */
    public char getPixel(Point p) {
        return pixels[p.y() - 1][p.x() - 1];
    }

    /**
     * Установка пикселя по 1-based координатам.
     */
    public void setPixel(Point p, char c) {
        pixels[p.y() - 1][p.x() - 1] = c;
    }

    /**
     * Рисует горизонтальную или вертикальную линию.
     *
     * Ключевой момент: я использую Math.min/max для нормализации координат.
     * Это означает, что линия рисуется корректно независимо от порядка точек:
     * - L 1 1 5 1  (слева направо)
     * - L 5 1 1 1  (справа налево)
     * - L 1 1 1 5  (сверху вниз)
     * - L 1 5 1 1  (снизу вверх)
     *
     * Все четыре варианта дадут одинаковый результат.
     * Это важно для UX — пользователь не должен думать о порядке точек.
     */
    public void drawLine(Point from, Point to) {
        var x1 = Math.min(from.x(), to.x());
        var x2 = Math.max(from.x(), to.x());
        var y1 = Math.min(from.y(), to.y());
        var y2 = Math.max(from.y(), to.y());

        for (var y = y1; y <= y2; y++) {
            for (var x = x1; x <= x2; x++) {
                setPixel(new Point(x, y), LINE_CHAR);
            }
        }
    }

    /**
     * Заливка области (Bucket Fill) — реализация алгоритма Flood Fill.
     *
     * ПОЧЕМУ BFS, А НЕ РЕКУРСИЯ?
     *
     * Первое, что приходит в голову — рекурсивный DFS:
     *
     *   void fill(x, y, target, color) {
     *       if (outOfBounds || pixel != target) return;
     *       setPixel(color);
     *       fill(x+1, y, ...);
     *       fill(x-1, y, ...);
     *       fill(x, y+1, ...);
     *       fill(x, y-1, ...);
     *   }
     *
     * Проблема: StackOverflowError на больших областях!
     * Если холст 1000x1000 и вся область пустая — это до миллиона рекурсивных вызовов.
     * Стек Java по умолчанию ~512KB, каждый кадр ~32 байта = ~16000 вызовов максимум.
     *
     * Поэтому я использую итеративный BFS с очередью:
     * - ArrayDeque для очереди — O(1) операции на обоих концах
     * - HashSet для visited — O(1) проверка "уже посещали?"
     *
     * Сложность: O(n) по времени и памяти, где n — количество закрашиваемых пикселей.
     *
     * ОПТИМИЗАЦИЯ: если целевой цвет совпадает с текущим — ничего не делаем.
     * Иначе зальём всю область тем же цветом, что была (бессмысленная работа).
     */
    public void fill(Point start, char color) {
        var target = getPixel(start);

        // Оптимизация: если цвет тот же — выходим
        if (target == color) {
            return;
        }

        // BFS с очередью вместо рекурсии
        var queue = new ArrayDeque<Point>();
        var visited = new HashSet<Point>();
        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            var p = queue.poll();
            setPixel(p, color);

            // Проверяем 4 соседних пикселя (без диагоналей)
            addIfNotVisited(queue, visited, p.moveX(1), target);   // право
            addIfNotVisited(queue, visited, p.moveX(-1), target);  // лево
            addIfNotVisited(queue, visited, p.moveY(1), target);   // вниз
            addIfNotVisited(queue, visited, p.moveY(-1), target);  // вверх
        }
    }

    /**
     * Вспомогательный метод для BFS: добавляем точку в очередь, если она валидна.
     *
     * Условия добавления:
     * 1. Точка внутри холста
     * 2. Ещё не посещали (нет в HashSet)
     * 3. Цвет пикселя совпадает с целевым (иначе это граница)
     *
     * Важно: добавляем в visited СРАЗУ при добавлении в очередь, а не при извлечении.
     * Иначе одна точка может попасть в очередь несколько раз от разных соседей.
     */
    private void addIfNotVisited(ArrayDeque<Point> queue, Set<Point> visited, Point p, char target) {
        if (!isOutOfBounds(p) && !visited.contains(p) && getPixel(p) == target) {
            queue.offer(p);
            visited.add(p);
        }
    }

    /**
     * Заполняет весь холст пробелами.
     */
    private void clear() {
        for (var row : pixels) {
            Arrays.fill(row, EMPTY_CHAR);
        }
    }
}
