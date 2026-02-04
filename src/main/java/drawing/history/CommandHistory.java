package drawing.history;

import drawing.canvas.Canvas;
import drawing.canvas.CanvasMemento;
import drawing.exception.DrawingException;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Менеджер истории для undo/redo операций.
 *
 * В терминах паттерна Memento это "Caretaker" — хранитель снимков.
 * Он не знает, что внутри CanvasMemento (это инкапсулировано),
 * просто хранит и отдаёт по запросу.
 *
 * СТРУКТУРА ДАННЫХ: ДВА СТЕКА
 *
 *   undoStack: [состояние до команды 1, состояние до команды 2, ...]
 *   redoStack: [состояние после undo 1, состояние после undo 2, ...]
 *
 * Пример работы:
 *
 *   1. Создаём холст (C 10 10)
 *      undoStack: [null]  // null = "холста не было"
 *      redoStack: []
 *
 *   2. Рисуем линию (L 1 1 5 1)
 *      undoStack: [null, пустой_холст]
 *      redoStack: []
 *
 *   3. Делаем Undo
 *      undoStack: [null]
 *      redoStack: [холст_с_линией]
 *
 *   4. Делаем Redo
 *      undoStack: [null, пустой_холст]
 *      redoStack: []
 *
 * ПОЧЕМУ LinkedList, А НЕ ArrayDeque?
 *
 * Потому что ArrayDeque не поддерживает null-элементы!
 * А нам нужен null для представления состояния "холста нет".
 *
 * Когда пользователь делает Undo после создания первого холста,
 * мы должны вернуться к состоянию "холст не создан" (null).
 *
 * ОГРАНИЧЕНИЕ РАЗМЕРА
 *
 * Максимум 50 состояний в истории. Почему?
 *
 * Каждый снимок — это полная копия холста. Для 1000x1000:
 * - 1 снимок ≈ 1MB
 * - 50 снимков ≈ 50MB
 *
 * Это разумный баланс между функциональностью и памятью.
 * При превышении лимита удаляем самые старые состояния.
 */
public final class CommandHistory {

    private static final int MAX_HISTORY_SIZE = 50;

    // LinkedList, потому что ArrayDeque не поддерживает null
    private final Deque<CanvasMemento> undoStack = new LinkedList<>();
    private final Deque<CanvasMemento> redoStack = new LinkedList<>();

    /**
     * Сохраняет текущее состояние перед выполнением команды.
     *
     * Вызывается из DrawingApp.run() для команд с modifiesCanvas() == true.
     *
     * ВАЖНО: очищаем redo-стек при новом действии!
     * Это стандартное поведение: после нового действия нельзя redo к старой истории.
     */
    public void saveState(Canvas canvas) {
        var memento = canvas == null ? null : CanvasMemento.from(canvas);
        undoStack.push(memento);

        // Ограничиваем размер истории
        if (undoStack.size() > MAX_HISTORY_SIZE) {
            undoStack.removeLast();  // Удаляем самое старое состояние
        }

        // Новое действие отменяет возможность redo
        redoStack.clear();
    }

    /**
     * Откатывает последнее сохранение (при ошибке выполнения команды).
     *
     * Это часть транзакционности: если команда упала после saveState(),
     * мы убираем только что сохранённое состояние, чтобы не засорять историю.
     */
    public void discardLastState() {
        if (!undoStack.isEmpty()) {
            undoStack.pop();
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Выполняет undo: возвращает предыдущее состояние.
     *
     * Алгоритм:
     * 1. Сохраняем ТЕКУЩЕЕ состояние в redo-стек (чтобы можно было вернуться)
     * 2. Берём ПРЕДЫДУЩЕЕ состояние из undo-стека
     * 3. Возвращаем его вызывающему коду для восстановления
     */
    public CanvasMemento undo(Canvas currentCanvas) {
        if (!canUndo()) {
            throw new DrawingException("Nothing to undo");
        }

        // Текущее состояние уходит в redo (может быть null!)
        var currentMemento = currentCanvas == null ? null : CanvasMemento.from(currentCanvas);
        redoStack.push(currentMemento);

        // Возвращаем предыдущее состояние (может быть null!)
        return undoStack.pop();
    }

    /**
     * Выполняет redo: возвращает отменённое состояние.
     *
     * Симметрично undo, только стеки меняются местами.
     */
    public CanvasMemento redo(Canvas currentCanvas) {
        if (!canRedo()) {
            throw new DrawingException("Nothing to redo");
        }

        var currentMemento = currentCanvas == null ? null : CanvasMemento.from(currentCanvas);
        undoStack.push(currentMemento);

        return redoStack.pop();
    }
}
