package drawing.canvas;

import org.junit.jupiter.api.Test;

import static drawing.canvas.DrawingConstants.EMPTY_CHAR;
import static drawing.canvas.DrawingConstants.LINE_CHAR;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests Memento pattern. Verifies defensive copying and snapshot immutability.
 */
class CanvasMementoTest {

    @Test
    void createsSnapshotOfCanvas() {
        var canvas = new Canvas(5, 4);
        canvas.drawLine(new Point(1, 1), new Point(3, 1));

        var memento = CanvasMemento.from(canvas);

        assertEquals(5, memento.width());
        assertEquals(4, memento.height());
    }

    @Test
    void snapshotIsImmutableWhenOriginalChanges() {
        var canvas = new Canvas(5, 4);
        canvas.drawLine(new Point(1, 1), new Point(3, 1));

        var memento = CanvasMemento.from(canvas);

        canvas.drawLine(new Point(1, 2), new Point(3, 2));

        var restored = memento.restore();
        assertEquals(LINE_CHAR, restored.getPixel(new Point(1, 1)));
        assertEquals(EMPTY_CHAR, restored.getPixel(new Point(1, 2)));
    }

    @Test
    void restoredCanvasIsIndependent() {
        var original = new Canvas(5, 4);
        original.drawLine(new Point(1, 1), new Point(3, 1));

        var memento = CanvasMemento.from(original);
        var restored = memento.restore();

        restored.drawLine(new Point(1, 2), new Point(3, 2));

        assertEquals(EMPTY_CHAR, original.getPixel(new Point(1, 2)));
        assertEquals(LINE_CHAR, restored.getPixel(new Point(1, 2)));
    }

    @Test
    void restoresEmptyCanvas() {
        var canvas = new Canvas(3, 3);
        var memento = CanvasMemento.from(canvas);

        var restored = memento.restore();

        for (int y = 1; y <= 3; y++) {
            for (int x = 1; x <= 3; x++) {
                assertEquals(EMPTY_CHAR, restored.getPixel(new Point(x, y)));
            }
        }
    }

    @Test
    void multipleRestoresAreIndependent() {
        var canvas = new Canvas(5, 4);
        canvas.drawLine(new Point(2, 2), new Point(4, 2));

        var memento = CanvasMemento.from(canvas);

        var restored1 = memento.restore();
        var restored2 = memento.restore();

        restored1.fill(new Point(1, 1), 'o');

        assertEquals('o', restored1.getPixel(new Point(1, 1)));
        assertEquals(EMPTY_CHAR, restored2.getPixel(new Point(1, 1)));
    }
}
