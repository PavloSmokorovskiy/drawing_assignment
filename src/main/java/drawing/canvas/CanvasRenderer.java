package drawing.canvas;

import static drawing.canvas.DrawingConstants.HORIZONTAL_BORDER;
import static drawing.canvas.DrawingConstants.VERTICAL_BORDER;

public final class CanvasRenderer {

    public String render(Canvas canvas) {
        var sb = new StringBuilder();
        int w = canvas.width();
        int h = canvas.height();
        String horizontalBorder = String.valueOf(HORIZONTAL_BORDER).repeat(w + 2) + '\n';

        sb.append(horizontalBorder);

        for (int y = 0; y < h; y++) {
            sb.append(VERTICAL_BORDER);
            for (int x = 0; x < w; x++) {
                sb.append(canvas.getPixelRaw(x, y));
            }
            sb.append(VERTICAL_BORDER).append('\n');
        }

        sb.append(horizontalBorder);
        return sb.toString();
    }
}
