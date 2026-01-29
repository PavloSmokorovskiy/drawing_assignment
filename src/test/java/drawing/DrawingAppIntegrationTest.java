package drawing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static drawing.DrawingConstants.EMPTY_CHAR;
import static drawing.DrawingConstants.LINE_CHAR;
import static org.junit.jupiter.api.Assertions.*;

class DrawingAppIntegrationTest {

    private DrawingContext context;
    private CommandParser parser;

    @BeforeEach
    void setUp() {
        context = new DrawingContext();
        parser = new CommandParser();
    }

    private void executeWithHistory(String input) {
        Command cmd = parser.parse(input);
        if (cmd.modifiesCanvas()) {
            context.getHistory().saveState(context.getCanvas());
        }
        cmd.execute(context);
    }

    @Nested
    class FullDrawingScenario {
        @Test
        void executesRequirementsExample() {
            executeWithHistory("C 20 4");
            assertEquals(20, context.getCanvas().width());
            assertEquals(4, context.getCanvas().height());

            executeWithHistory("L 1 2 6 2");
            for (int x = 1; x <= 6; x++) {
                assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(x, 2)));
            }

            executeWithHistory("L 6 3 6 4");
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(6, 3)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(6, 4)));

            executeWithHistory("R 14 1 18 3");
            for (int x = 14; x <= 18; x++) {
                assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(x, 1)));
                assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(x, 3)));
            }
            for (int y = 1; y <= 3; y++) {
                assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(14, y)));
                assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(18, y)));
            }

            executeWithHistory("B 10 3 o");
            assertEquals('o', context.getCanvas().getPixel(new Point(10, 3)));
            assertEquals('o', context.getCanvas().getPixel(new Point(1, 1)));
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(16, 2)));
        }
    }

    @Nested
    class UndoRedoIntegration {
        @Test
        void undoRedoBasicFlow() {
            executeWithHistory("C 5 4");
            executeWithHistory("L 1 1 3 1");

            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(2, 1)));

            parser.parse("U").execute(context);
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(2, 1)));

            parser.parse("Z").execute(context);
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(2, 1)));
        }

        @Test
        void multipleUndosAndRedos() {
            executeWithHistory("C 5 4");
            executeWithHistory("L 1 1 3 1");
            executeWithHistory("L 1 2 3 2");
            executeWithHistory("L 1 3 3 3");

            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(1, 1)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(1, 2)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(1, 3)));

            parser.parse("U").execute(context);
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 3)));

            parser.parse("U").execute(context);
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 2)));

            parser.parse("Z").execute(context);
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(1, 2)));

            executeWithHistory("L 1 4 3 4");

            assertThrows(DrawingException.class, () -> parser.parse("Z").execute(context));
        }

        @Test
        void undoThenDifferentAction() {
            executeWithHistory("C 5 4");
            executeWithHistory("L 1 1 3 1");

            parser.parse("U").execute(context);

            executeWithHistory("L 1 2 3 2");

            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 1)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(1, 2)));
        }

        @Test
        void undoRectangle() {
            executeWithHistory("C 10 6");
            executeWithHistory("R 2 2 5 4");

            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(2, 2)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(5, 4)));

            parser.parse("U").execute(context);

            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(2, 2)));
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(5, 4)));
        }

        @Test
        void undoFill() {
            executeWithHistory("C 5 4");
            executeWithHistory("B 1 1 o");

            assertEquals('o', context.getCanvas().getPixel(new Point(3, 3)));

            parser.parse("U").execute(context);

            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(3, 3)));
        }

        @Test
        void undoToNullCanvas() {
            executeWithHistory("C 5 4");

            assertNotNull(context.getCanvas());

            parser.parse("U").execute(context);

            assertNull(context.getCanvas());
        }
    }

    @Nested
    class CombinedCommands {
        @Test
        void rectangleWithFillInside() {
            executeWithHistory("C 10 6");
            executeWithHistory("R 2 2 8 5");
            executeWithHistory("B 5 3 o");

            assertEquals('o', context.getCanvas().getPixel(new Point(5, 3)));
            assertEquals('o', context.getCanvas().getPixel(new Point(3, 4)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(2, 2)));
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 1)));
        }

        @Test
        void fillOutsideRectangle() {
            executeWithHistory("C 10 6");
            executeWithHistory("R 3 2 7 5");
            executeWithHistory("B 1 1 o");

            assertEquals('o', context.getCanvas().getPixel(new Point(1, 1)));
            assertEquals('o', context.getCanvas().getPixel(new Point(9, 6)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(3, 2)));
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(5, 3)));
        }

        @Test
        void multipleShapesWithUndo() {
            executeWithHistory("C 10 6");
            executeWithHistory("L 1 1 5 1");
            executeWithHistory("R 7 2 9 4");
            executeWithHistory("B 1 3 o");

            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(3, 1)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(8, 2)));
            assertEquals('o', context.getCanvas().getPixel(new Point(1, 3)));

            parser.parse("U").execute(context);

            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(3, 1)));
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(8, 2)));
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 3)));

            parser.parse("U").execute(context);

            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(3, 1)));
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(8, 2)));
        }
    }

    @Nested
    class ErrorRecovery {
        @Test
        void commandAfterErrorContinuesNormally() {
            executeWithHistory("C 5 4");

            assertThrows(DrawingException.class,
                    () -> parser.parse("L 0 1 3 1").execute(context));

            executeWithHistory("L 1 1 3 1");
            assertEquals(LINE_CHAR, context.getCanvas().getPixel(new Point(1, 1)));
        }

        @Test
        void undoAfterErrorWorks() {
            executeWithHistory("C 5 4");
            executeWithHistory("L 1 1 3 1");

            assertThrows(DrawingException.class,
                    () -> parser.parse("L 0 2 3 2").execute(context));

            parser.parse("U").execute(context);
            assertEquals(EMPTY_CHAR, context.getCanvas().getPixel(new Point(1, 1)));
        }
    }
}
