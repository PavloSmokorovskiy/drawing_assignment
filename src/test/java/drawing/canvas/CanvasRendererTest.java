package drawing.canvas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CanvasRendererTest {

    private CanvasRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new CanvasRenderer();
    }

    @Test
    void rendersEmptyCanvas() {
        var canvas = new Canvas(4, 2);

        var result = renderer.render(canvas);

        var expected =
                """
                        ------
                        |    |
                        |    |
                        ------
                        """;

        assertEquals(expected, result);
    }

    @Test
    void rendersCanvasWithLine() {
        var canvas = new Canvas(4, 2);
        canvas.drawLine(new Point(1, 1), new Point(4, 1));

        var result = renderer.render(canvas);

        var expected =
                """
                        ------
                        |xxxx|
                        |    |
                        ------
                        """;

        assertEquals(expected, result);
    }

    @Test
    void rendersCanvasWithFill() {
        var canvas = new Canvas(4, 2);
        canvas.fill(new Point(1, 1), 'o');

        var result = renderer.render(canvas);

        var expected =
                """
                        ------
                        |oooo|
                        |oooo|
                        ------
                        """;

        assertEquals(expected, result);
    }

    @Test
    void rendersComplexDrawing() {
        var canvas = new Canvas(5, 3);
        canvas.drawLine(new Point(1, 1), new Point(5, 1));
        canvas.drawLine(new Point(3, 1), new Point(3, 3));

        var result = renderer.render(canvas);

        var expected =
                """
                        -------
                        |xxxxx|
                        |  x  |
                        |  x  |
                        -------
                        """;

        assertEquals(expected, result);
    }

    @Test
    void rendersBordersCorrectly() {
        var canvas = new Canvas(2, 1);

        var result = renderer.render(canvas);

        assertTrue(result.startsWith("----\n"));
        assertTrue(result.contains("|"));
        assertTrue(result.endsWith("----\n"));
    }
}
