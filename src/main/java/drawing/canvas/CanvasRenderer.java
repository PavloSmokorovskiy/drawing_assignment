package drawing.canvas;

import static drawing.canvas.DrawingConstants.HORIZONTAL_BORDER;
import static drawing.canvas.DrawingConstants.VERTICAL_BORDER;

/**
 * Преобразует Canvas в текстовое представление с рамками.
 *
 * ЗАЧЕМ ОТДЕЛЬНЫЙ КЛАСС?
 *
 * Можно было бы сделать Canvas.toString(). Но это плохо:
 *
 * 1. toString() обычно используется для отладки, а не для вывода пользователю.
 *
 * 2. Если понадобится другой формат (JSON, HTML), придётся менять Canvas.
 *    С отдельным Renderer можно создать JsonRenderer, HtmlRenderer — не трогая Canvas.
 *
 * 3. Canvas хранит ДАННЫЕ (пиксели), Renderer занимается ПРЕДСТАВЛЕНИЕМ.
 *    Это Single Responsibility Principle.
 *
 * РАМКИ — ЭТО НЕ ДАННЫЕ
 *
 * Обратите внимание: рамки (-----, |) добавляются ТОЛЬКО при рендеринге.
 * Canvas их НЕ хранит. Это важно:
 *
 * - Координата (1,1) — это реальный верхний левый пиксель, а не рамка
 * - Размер 20x4 означает 20x4 пикселей, а не 22x6 с рамками
 * - При сохранении в файл рамки добавляются, при undo/redo — нет
 *
 * Это решает замечание из ревью: "confusion between business data and formatting".
 */
public final class CanvasRenderer {

    /**
     * Рендерит холст в текст с ASCII-рамками.
     *
     * Формат вывода:
     * ----------------------
     * |    pixels          |
     * |    pixels          |
     * ----------------------
     *
     * StringBuilder используется вместо конкатенации строк.
     * Для холста 1000x1000 это критично — конкатенация O(n²), StringBuilder O(n).
     */
    public String render(Canvas canvas) {
        var sb = new StringBuilder();
        var w = canvas.width();
        var h = canvas.height();

        // Верхняя рамка: +2 символа (левый и правый бордер)
        var horizontalBorder = String.valueOf(HORIZONTAL_BORDER).repeat(w + 2) + '\n';

        sb.append(horizontalBorder);

        // Строки с пикселями
        for (var y = 0; y < h; y++) {
            sb.append(VERTICAL_BORDER);
            for (var x = 0; x < w; x++) {
                // getPixelRaw — прямой доступ (0-indexed), без конвертации координат
                sb.append(canvas.getPixelRaw(x, y));
            }
            sb.append(VERTICAL_BORDER).append('\n');
        }

        sb.append(horizontalBorder);
        return sb.toString();
    }
}
