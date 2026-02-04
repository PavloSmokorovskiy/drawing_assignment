package drawing.command;

import drawing.context.DrawingContext;

/**
 * Команда отмены последнего действия (Undo).
 *
 * ПОЧЕМУ MEMENTO, А НЕ ОБРАТНЫЕ ОПЕРАЦИИ?
 *
 * Альтернативный подход к undo — каждая команда знает, как себя "откатить":
 *
 *   interface Command {
 *       void execute(...);
 *       void undo(...);  // обратная операция
 *   }
 *
 * Проблемы этого подхода:
 *
 * 1) Сложность: как "откатить" BucketFill? Нужно помнить ВСЕ пиксели, которые
 *    были изменены, и их предыдущие значения. Это фактически тот же Memento.
 *
 * 2) Хрупкость: легко написать неправильный undo, который восстанавливает
 *    состояние некорректно. Особенно для сложных операций.
 *
 * С Memento всё просто: сохраняем полный снимок ДО операции, восстанавливаем при undo.
 * Да, это больше памяти (полная копия холста), но:
 * - Гарантированно корректное восстановление
 * - Единообразная реализация для всех команд
 * - Код проще и понятнее
 *
 * ПОЧЕМУ modifiesCanvas() ВОЗВРАЩАЕТ false?
 *
 * Потому что Undo САМО управляет историей через CommandHistory.
 * Если бы DrawingApp сохранял состояние перед Undo, мы бы получили:
 * - Состояние A (текущее)
 * - Сохраняем A в undo-стек
 * - Выполняем Undo, который берёт B из стека и кладёт A в redo
 * - Теперь A дважды в истории — бардак!
 */
public record UndoCommand() implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        // Получаем предыдущее состояние из истории
        var previousState = ctx.getHistory().undo(ctx.getCanvas());

        // Memento может быть null, если отменяем создание первого холста
        var restoredCanvas = previousState == null ? null : previousState.restore();
        ctx.setCanvas(restoredCanvas);
    }

    @Override
    public boolean modifiesCanvas() {
        return false;  // Управляем историей сами
    }
}
