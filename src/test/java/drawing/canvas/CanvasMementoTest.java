package drawing.canvas;

import org.junit.jupiter.api.Test;

import static drawing.canvas.DrawingConstants.EMPTY_CHAR;
import static drawing.canvas.DrawingConstants.LINE_CHAR;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CanvasMementoTest {

    @Test
    void createsSnapshotOfCanvas() {
        Canvas canvas = new Canvas(5, 4);
        canvas.drawLine(new Point(1, 1), new Point(3, 1));

        CanvasMemento memento = CanvasMemento.from(canvas);

        assertEquals(5, memento.width());
        assertEquals(4, memento.height());
    }

    @Test
    void snapshotIsImmutableWhenOriginalChanges() {
        Canvas canvas = new Canvas(5, 4);
        canvas.drawLine(new Point(1, 1), new Point(3, 1));

        CanvasMemento memento = CanvasMemento.from(canvas);

        canvas.drawLine(new Point(1, 2), new Point(3, 2));

        Canvas restored = memento.restore();
        assertEquals(LINE_CHAR, restored.getPixel(new Point(1, 1)));
        assertEquals(EMPTY_CHAR, restored.getPixel(new Point(1, 2)));
    }

    @Test
    void restoredCanvasIsIndependent() {
        Canvas original = new Canvas(5, 4);
        original.drawLine(new Point(1, 1), new Point(3, 1));

        CanvasMemento memento = CanvasMemento.from(original);
        Canvas restored = memento.restore();

        restored.drawLine(new Point(1, 2), new Point(3, 2));

        assertEquals(EMPTY_CHAR, original.getPixel(new Point(1, 2)));
        assertEquals(LINE_CHAR, restored.getPixel(new Point(1, 2)));
    }

    @Test
    void restoresEmptyCanvas() {
        Canvas canvas = new Canvas(3, 3);
        CanvasMemento memento = CanvasMemento.from(canvas);

        Canvas restored = memento.restore();

        for (int y = 1; y <= 3; y++) {
            for (int x = 1; x <= 3; x++) {
                assertEquals(EMPTY_CHAR, restored.getPixel(new Point(x, y)));
            }
        }
    }

    @Test
    void multipleRestoresAreIndependent() {
        Canvas canvas = new Canvas(5, 4);
        canvas.drawLine(new Point(2, 2), new Point(4, 2));

        CanvasMemento memento = CanvasMemento.from(canvas);

        Canvas restored1 = memento.restore();
        Canvas restored2 = memento.restore();

        restored1.fill(new Point(1, 1), 'o');

        assertEquals('o', restored1.getPixel(new Point(1, 1)));
        assertEquals(EMPTY_CHAR, restored2.getPixel(new Point(1, 1)));
    }
}
