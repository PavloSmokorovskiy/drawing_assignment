package drawing.canvas;

/**
 * Снимок состояния холста для undo/redo.
 *
 * ПАТТЕРН MEMENTO
 *
 * Memento — это способ сохранить внутреннее состояние объекта без нарушения
 * инкапсуляции, чтобы потом можно было восстановить объект.
 *
 * В классическом Memento три роли:
 * - Originator (Canvas) — объект, состояние которого сохраняем
 * - Memento (CanvasMemento) — "снимок" состояния
 * - Caretaker (CommandHistory) — хранит снимки, не зная их структуру
 *
 * ПОЧЕМУ НЕ ПРОСТО КЛОНИРОВАТЬ CANVAS?
 *
 * Можно было бы хранить копии Canvas напрямую:
 *   Deque<Canvas> undoStack;
 *
 * Но это плохо:
 * 1. Canvas — изменяемый объект. Без глубокого копирования история будет испорчена.
 * 2. Нужен Canvas.clone(), который легко реализовать неправильно.
 * 3. Memento явно показывает намерение: "это снимок для восстановления".
 *
 * DEFENSIVE COPYING
 *
 * И в from(), и в restore() я делаю копию массива пикселей.
 *
 * В from(): чтобы изменения в Canvas после снимка не повлияли на снимок.
 * В restore(): чтобы изменения в восстановленном Canvas не повлияли на снимок
 *              (нужно для Redo — снимок может использоваться повторно).
 */
public final class CanvasMemento {

    private final int width;
    private final int height;
    private final char[][] pixels;

    /**
     * Приватный конструктор — создание только через from().
     */
    private CanvasMemento(int width, int height, char[][] pixels) {
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
     * Создаёт снимок текущего состояния Canvas.
     *
     * Это factory method вместо публичного конструктора, потому что:
     * - Название from(canvas) понятнее, чем new CanvasMemento(canvas)
     * - Инкапсулирует получение данных из Canvas
     */
    public static CanvasMemento from(Canvas canvas) {
        return new CanvasMemento(canvas.width(), canvas.height(), canvas.copyPixels());
    }

    /**
     * Восстанавливает Canvas из снимка.
     *
     * Возвращает НОВЫЙ объект Canvas, а не модифицирует существующий.
     * Это проще и безопаснее, чем canvas.restoreFrom(memento).
     */
    public Canvas restore() {
        // Копируем pixels, чтобы повторные restore() работали корректно
        return new Canvas(width, height, PixelArrays.copy(pixels));
    }
}
