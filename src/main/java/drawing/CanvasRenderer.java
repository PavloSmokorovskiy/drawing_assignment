package drawing;

import static drawing.DrawingConstants.*;

public final class CanvasRenderer {
    public String render(Canvas canvas) {
        var sb = new StringBuilder();
        int w = canvas.width(), h = canvas.height();

        sb.append(String.valueOf(HORIZONTAL_BORDER).repeat(w + 2)).append('\n');

        for (int y = 0; y < h; y++) {
            sb.append(VERTICAL_BORDER);
            for (int x = 0; x < w; x++) {
                sb.append(canvas.pixels()[y][x]);
            }
            sb.append(VERTICAL_BORDER).append('\n');
        }

        sb.append(String.valueOf(HORIZONTAL_BORDER).repeat(w + 2)).append('\n');
        return sb.toString();
    }
}
