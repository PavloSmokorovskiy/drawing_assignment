package drawing.command;

import drawing.context.DrawingContext;

/**
 * Команда повтора отменённого действия (Redo).
 *
 * Работает симметрично с Undo:
 * - Undo: берём из undo-стека, кладём текущее в redo-стек
 * - Redo: берём из redo-стека, кладём текущее в undo-стек
 *
 * ВАЖНЫЙ НЮАНС: ОЧИСТКА REDO-СТЕКА
 *
 * Когда пользователь делает новое действие после Undo, redo-стек очищается.
 * Это стандартное поведение во всех редакторах:
 *
 *   Draw A → Draw B → Undo → Undo → Draw C
 *
 * После Draw C нельзя сделать Redo обратно к A и B — они "потеряны".
 * Это интуитивно понятно: мы начали новую "ветку" истории.
 *
 * Очистка происходит в CommandHistory.saveState() — при любом новом
 * действии, которое модифицирует холст.
 */
public record RedoCommand() implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        var nextState = ctx.getHistory().redo(ctx.getCanvas());
        var restoredCanvas = nextState == null ? null : nextState.restore();
        ctx.setCanvas(restoredCanvas);
    }

    @Override
    public boolean modifiesCanvas() {
        return false;  // Управляем историей сами
    }
}
