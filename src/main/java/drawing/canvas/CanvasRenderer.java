package drawing.canvas;

import static drawing.canvas.DrawingConstants.HORIZONTAL_BORDER;
import static drawing.canvas.DrawingConstants.VERTICAL_BORDER;

/**
 * Single Responsibility: rendering only. Canvas stores pixels, Renderer adds borders.
 */
public final class CanvasRenderer {

    public String render(Canvas canvas) {
        var sb = new StringBuilder();
        var w = canvas.width();
        var h = canvas.height();
        var horizontalBorder = String.valueOf(HORIZONTAL_BORDER).repeat(w + 2) + '\n';

        sb.append(horizontalBorder);

        for (var y = 0; y < h; y++) {
            sb.append(VERTICAL_BORDER);
            for (var x = 0; x < w; x++) {
                sb.append(canvas.getPixelRaw(x, y));
            }
            sb.append(VERTICAL_BORDER).append('\n');
        }

        sb.append(horizontalBorder);
        return sb.toString();
    }
}
