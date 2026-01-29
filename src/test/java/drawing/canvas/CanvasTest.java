package drawing.canvas;

import drawing.exception.DrawingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static drawing.canvas.DrawingConstants.EMPTY_CHAR;
import static drawing.canvas.DrawingConstants.LINE_CHAR;
import static org.junit.jupiter.api.Assertions.*;

class CanvasTest {

    private Canvas canvas;

    @BeforeEach
    void setUp() {
        canvas = new Canvas(5, 4);
    }

    @Nested
    class Construction {
        @Test
        void createsCanvasWithCorrectDimensions() {
            assertEquals(5, canvas.width());
            assertEquals(4, canvas.height());
        }

        @Test
        void initializesAllPixelsToEmpty() {
            for (int y = 1; y <= 4; y++) {
                for (int x = 1; x <= 5; x++) {
                    assertEquals(EMPTY_CHAR, canvas.getPixel(new Point(x, y)));
                }
            }
        }
    }

    @Nested
    class DrawLine {
        @Test
        void drawsHorizontalLine() {
            canvas.drawLine(new Point(1, 2), new Point(4, 2));

            assertEquals(LINE_CHAR, canvas.getPixel(new Point(1, 2)));
            assertEquals(LINE_CHAR, canvas.getPixel(new Point(2, 2)));
            assertEquals(LINE_CHAR, canvas.getPixel(new Point(3, 2)));
            assertEquals(LINE_CHAR, canvas.getPixel(new Point(4, 2)));
            assertEquals(EMPTY_CHAR, canvas.getPixel(new Point(5, 2)));
            assertEquals(EMPTY_CHAR, canvas.getPixel(new Point(1, 1)));
        }

        @Test
        void drawsVerticalLine() {
            canvas.drawLine(new Point(2, 1), new Point(2, 4));

            for (int y = 1; y <= 4; y++) {
                assertEquals(LINE_CHAR, canvas.getPixel(new Point(2, y)));
            }
            assertEquals(EMPTY_CHAR, canvas.getPixel(new Point(1, 1)));
            assertEquals(EMPTY_CHAR, canvas.getPixel(new Point(3, 1)));
        }

        @Test
        void handlesReversedHorizontalCoordinates() {
            canvas.drawLine(new Point(4, 2), new Point(1, 2));

            for (int x = 1; x <= 4; x++) {
                assertEquals(LINE_CHAR, canvas.getPixel(new Point(x, 2)));
            }
        }

        @Test
        void handlesReversedVerticalCoordinates() {
            canvas.drawLine(new Point(2, 4), new Point(2, 1));

            for (int y = 1; y <= 4; y++) {
                assertEquals(LINE_CHAR, canvas.getPixel(new Point(2, y)));
            }
        }

        @Test
        void drawsSinglePoint() {
            canvas.drawLine(new Point(3, 2), new Point(3, 2));

            assertEquals(LINE_CHAR, canvas.getPixel(new Point(3, 2)));
            assertEquals(EMPTY_CHAR, canvas.getPixel(new Point(2, 2)));
            assertEquals(EMPTY_CHAR, canvas.getPixel(new Point(4, 2)));
        }
    }

    @Nested
    class Fill {
        @Test
        void fillsEmptyCanvas() {
            canvas.fill(new Point(1, 1), 'o');

            for (int y = 1; y <= 4; y++) {
                for (int x = 1; x <= 5; x++) {
                    assertEquals('o', canvas.getPixel(new Point(x, y)));
                }
            }
        }

        @Test
        void fillStopsAtLineBoundary() {
            canvas.drawLine(new Point(3, 1), new Point(3, 4));
            canvas.fill(new Point(1, 1), 'o');

            assertEquals('o', canvas.getPixel(new Point(1, 1)));
            assertEquals('o', canvas.getPixel(new Point(2, 2)));
            assertEquals(LINE_CHAR, canvas.getPixel(new Point(3, 1)));
            assertEquals(EMPTY_CHAR, canvas.getPixel(new Point(4, 1)));
            assertEquals(EMPTY_CHAR, canvas.getPixel(new Point(5, 2)));
        }

        @Test
        void fillInsideRectangle() {
            canvas.drawLine(new Point(1, 1), new Point(5, 1));
            canvas.drawLine(new Point(1, 4), new Point(5, 4));
            canvas.drawLine(new Point(1, 1), new Point(1, 4));
            canvas.drawLine(new Point(5, 1), new Point(5, 4));

            canvas.fill(new Point(3, 2), 'o');

            assertEquals('o', canvas.getPixel(new Point(2, 2)));
            assertEquals('o', canvas.getPixel(new Point(3, 3)));
            assertEquals(LINE_CHAR, canvas.getPixel(new Point(1, 1)));
            assertEquals(LINE_CHAR, canvas.getPixel(new Point(5, 4)));
        }

        @Test
        void fillWithSameColorDoesNothing() {
            canvas.fill(new Point(1, 1), 'o');
            canvas.fill(new Point(3, 3), 'o');

            for (int y = 1; y <= 4; y++) {
                for (int x = 1; x <= 5; x++) {
                    assertEquals('o', canvas.getPixel(new Point(x, y)));
                }
            }
        }

        @Test
        void fillOnLineDoesNotSpread() {
            canvas.drawLine(new Point(1, 2), new Point(5, 2));
            canvas.fill(new Point(3, 2), 'o');

            assertEquals('o', canvas.getPixel(new Point(1, 2)));
            assertEquals('o', canvas.getPixel(new Point(5, 2)));
            assertEquals(EMPTY_CHAR, canvas.getPixel(new Point(1, 1)));
            assertEquals(EMPTY_CHAR, canvas.getPixel(new Point(1, 3)));
        }
    }

    @Nested
    class EdgeCases {
        @Test
        void handles1x1Canvas() {
            var tinyCanvas = new Canvas(1, 1);
            assertEquals(1, tinyCanvas.width());
            assertEquals(1, tinyCanvas.height());
            assertEquals(EMPTY_CHAR, tinyCanvas.getPixel(new Point(1, 1)));
        }

        @Test
        void drawLineOn1x1Canvas() {
            var tinyCanvas = new Canvas(1, 1);
            tinyCanvas.drawLine(new Point(1, 1), new Point(1, 1));
            assertEquals(LINE_CHAR, tinyCanvas.getPixel(new Point(1, 1)));
        }

        @Test
        void fillOn1x1Canvas() {
            var tinyCanvas = new Canvas(1, 1);
            tinyCanvas.fill(new Point(1, 1), '#');
            assertEquals('#', tinyCanvas.getPixel(new Point(1, 1)));
        }

        @Test
        void handlesLargeCanvas() {
            var largeCanvas = new Canvas(200, 100);
            assertEquals(200, largeCanvas.width());
            assertEquals(100, largeCanvas.height());

            largeCanvas.drawLine(new Point(1, 50), new Point(200, 50));
            assertEquals(LINE_CHAR, largeCanvas.getPixel(new Point(1, 50)));
            assertEquals(LINE_CHAR, largeCanvas.getPixel(new Point(200, 50)));

            largeCanvas.fill(new Point(100, 1), 'A');
            assertEquals('A', largeCanvas.getPixel(new Point(1, 1)));
            assertEquals('A', largeCanvas.getPixel(new Point(100, 49)));
            assertEquals(LINE_CHAR, largeCanvas.getPixel(new Point(100, 50)));
            assertEquals(EMPTY_CHAR, largeCanvas.getPixel(new Point(100, 51)));
        }

        @Test
        void fillWithDifferentColors() {
            canvas.drawLine(new Point(3, 1), new Point(3, 4));
            canvas.fill(new Point(1, 1), '#');
            canvas.fill(new Point(5, 1), '*');

            assertEquals('#', canvas.getPixel(new Point(1, 1)));
            assertEquals('#', canvas.getPixel(new Point(2, 4)));
            assertEquals(LINE_CHAR, canvas.getPixel(new Point(3, 2)));
            assertEquals('*', canvas.getPixel(new Point(4, 1)));
            assertEquals('*', canvas.getPixel(new Point(5, 4)));
        }

        @Test
        void overlappingLinesDrawLastOnTop() {
            canvas.drawLine(new Point(1, 2), new Point(5, 2));
            canvas.drawLine(new Point(3, 1), new Point(3, 4));

            assertEquals(LINE_CHAR, canvas.getPixel(new Point(3, 2)));
            assertEquals(LINE_CHAR, canvas.getPixel(new Point(1, 2)));
            assertEquals(LINE_CHAR, canvas.getPixel(new Point(3, 1)));
        }

        @Test
        void multipleOverlappingRectangles() {
            canvas.drawLine(new Point(1, 1), new Point(4, 1));
            canvas.drawLine(new Point(1, 3), new Point(4, 3));
            canvas.drawLine(new Point(1, 1), new Point(1, 3));
            canvas.drawLine(new Point(4, 1), new Point(4, 3));

            canvas.drawLine(new Point(2, 2), new Point(5, 2));
            canvas.drawLine(new Point(2, 4), new Point(5, 4));
            canvas.drawLine(new Point(2, 2), new Point(2, 4));
            canvas.drawLine(new Point(5, 2), new Point(5, 4));

            assertEquals(LINE_CHAR, canvas.getPixel(new Point(2, 2)));
            assertEquals(LINE_CHAR, canvas.getPixel(new Point(4, 2)));
            assertEquals(LINE_CHAR, canvas.getPixel(new Point(2, 3)));
        }

        @Test
        void fillDoesNotCrossLines() {
            canvas.drawLine(new Point(3, 1), new Point(3, 4));

            canvas.fill(new Point(1, 2), 'L');

            canvas.fill(new Point(5, 2), 'R');

            assertEquals('L', canvas.getPixel(new Point(1, 1)));
            assertEquals('L', canvas.getPixel(new Point(2, 4)));
            assertEquals(LINE_CHAR, canvas.getPixel(new Point(3, 2)));
            assertEquals('R', canvas.getPixel(new Point(4, 1)));
            assertEquals('R', canvas.getPixel(new Point(5, 4)));
        }
    }

    @Nested
    class BoundsValidation {
        @Test
        void throwsForXTooSmall() {
            assertThrows(DrawingException.class,
                    () -> canvas.validateBounds(new Point(0, 1)));
        }

        @Test
        void throwsForXTooLarge() {
            assertThrows(DrawingException.class,
                    () -> canvas.validateBounds(new Point(6, 1)));
        }

        @Test
        void throwsForYTooSmall() {
            assertThrows(DrawingException.class,
                    () -> canvas.validateBounds(new Point(1, 0)));
        }

        @Test
        void throwsForYTooLarge() {
            assertThrows(DrawingException.class,
                    () -> canvas.validateBounds(new Point(1, 5)));
        }

        @Test
        void acceptsValidBoundaryPoints() {
            assertDoesNotThrow(() -> {
                canvas.validateBounds(new Point(1, 1));
                canvas.validateBounds(new Point(5, 4));
                canvas.validateBounds(new Point(1, 4));
                canvas.validateBounds(new Point(5, 1));
            });
        }

        @Test
        void validatesMultiplePoints() {
            assertThrows(DrawingException.class,
                    () -> canvas.validateBounds(new Point(1, 1), new Point(6, 1)));
        }
    }
}
