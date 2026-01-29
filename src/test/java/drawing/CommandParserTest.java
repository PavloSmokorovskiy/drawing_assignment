package drawing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    private CommandParser parser;

    @BeforeEach
    void setUp() {
        parser = new CommandParser();
    }

    @Nested
    class CreateCanvas {
        @Test
        void parsesValidCommand() {
            Command cmd = parser.parse("C 20 4");

            assertInstanceOf(CreateCanvasCommand.class, cmd);
            CreateCanvasCommand c = (CreateCanvasCommand) cmd;
            assertEquals(20, c.width());
            assertEquals(4, c.height());
        }

        @Test
        void handlesCaseInsensitivity() {
            Command cmd = parser.parse("c 10 5");
            assertInstanceOf(CreateCanvasCommand.class, cmd);
        }

        @Test
        void handlesExtraWhitespace() {
            Command cmd = parser.parse("  C   20   4  ");
            assertInstanceOf(CreateCanvasCommand.class, cmd);
        }
    }

    @Nested
    class DrawLine {
        @Test
        void parsesValidCommand() {
            Command cmd = parser.parse("L 1 2 6 2");

            assertInstanceOf(DrawLineCommand.class, cmd);
            DrawLineCommand l = (DrawLineCommand) cmd;
            assertEquals(new Point(1, 2), l.from());
            assertEquals(new Point(6, 2), l.to());
        }
    }

    @Nested
    class DrawRectangle {
        @Test
        void parsesValidCommand() {
            Command cmd = parser.parse("R 14 1 18 3");

            assertInstanceOf(DrawRectangleCommand.class, cmd);
            DrawRectangleCommand r = (DrawRectangleCommand) cmd;
            assertEquals(new Point(14, 1), r.corner1());
            assertEquals(new Point(18, 3), r.corner2());
        }
    }

    @Nested
    class BucketFill {
        @Test
        void parsesValidCommand() {
            Command cmd = parser.parse("B 10 3 o");

            assertInstanceOf(BucketFillCommand.class, cmd);
            BucketFillCommand b = (BucketFillCommand) cmd;
            assertEquals(new Point(10, 3), b.point());
            assertEquals('o', b.color());
        }

        @Test
        void rejectsMultiCharacterColor() {
            DrawingException ex = assertThrows(DrawingException.class,
                    () -> parser.parse("B 10 3 oo"));
            assertTrue(ex.getMessage().contains("single character"));
        }

        @Test
        void rejectsLineCharAsColor() {
            DrawingException ex = assertThrows(DrawingException.class,
                    () -> parser.parse("B 10 3 x"));
            assertTrue(ex.getMessage().contains("reserved for lines"));
        }
    }

    @Nested
    class Help {
        @Test
        void parsesHelpCommand() {
            Command cmd = parser.parse("H");
            assertInstanceOf(HelpCommand.class, cmd);
        }

        @Test
        void helpCommandCaseInsensitive() {
            assertInstanceOf(HelpCommand.class, parser.parse("h"));
        }
    }

    @Nested
    class Save {
        @Test
        void parsesSaveCommand() {
            Command cmd = parser.parse("S output.txt");

            assertInstanceOf(SaveCommand.class, cmd);
            SaveCommand s = (SaveCommand) cmd;
            assertEquals("output.txt", s.filename());
        }

        @Test
        void rejectsSaveWithoutFilename() {
            DrawingException ex = assertThrows(DrawingException.class,
                    () -> parser.parse("S"));
            assertTrue(ex.getMessage().contains("Usage"));
        }
    }

    @Nested
    class UndoRedo {
        @Test
        void parsesUndoCommand() {
            Command cmd = parser.parse("U");

            assertInstanceOf(UndoCommand.class, cmd);
            assertFalse(cmd.modifiesCanvas());
        }

        @Test
        void parsesRedoCommand() {
            Command cmd = parser.parse("Z");

            assertInstanceOf(RedoCommand.class, cmd);
            assertFalse(cmd.modifiesCanvas());
        }

        @Test
        void undoCommandCaseInsensitive() {
            assertInstanceOf(UndoCommand.class, parser.parse("u"));
        }

        @Test
        void redoCommandCaseInsensitive() {
            assertInstanceOf(RedoCommand.class, parser.parse("z"));
        }
    }

    @Nested
    class Quit {
        @Test
        void parsesQuitCommand() {
            Command cmd = parser.parse("Q");

            assertInstanceOf(drawing.QuitCommand.class, cmd);
            assertTrue(cmd.shouldQuit());
        }
    }

    @Nested
    class ErrorHandling {
        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        void rejectsEmptyInput(String input) {
            assertThrows(DrawingException.class, () -> parser.parse(input));
        }

        @Test
        void rejectsNullInput() {
            assertThrows(DrawingException.class, () -> parser.parse(null));
        }

        @Test
        void rejectsUnknownCommand() {
            DrawingException ex = assertThrows(DrawingException.class,
                    () -> parser.parse("X 1 2 3"));
            assertTrue(ex.getMessage().contains("Unknown command"));
        }

        @Test
        void rejectsTooFewArgumentsForCanvas() {
            DrawingException ex = assertThrows(DrawingException.class,
                    () -> parser.parse("C 20"));
            assertTrue(ex.getMessage().contains("Usage"));
        }

        @Test
        void rejectsTooManyArgumentsForCanvas() {
            assertThrows(DrawingException.class, () -> parser.parse("C 20 4 5"));
        }

        @Test
        void rejectsTooFewArgumentsForLine() {
            assertThrows(DrawingException.class, () -> parser.parse("L 1 2 3"));
        }

        @Test
        void rejectsNonNumericCoordinates() {
            DrawingException ex = assertThrows(DrawingException.class,
                    () -> parser.parse("C abc 4"));
            assertTrue(ex.getMessage().contains("number"));
        }

        @Test
        void rejectsZeroWidth() {
            DrawingException ex = assertThrows(DrawingException.class,
                    () -> parser.parse("C 0 4"));
            assertTrue(ex.getMessage().contains("positive"));
        }

        @Test
        void rejectsNegativeHeight() {
            DrawingException ex = assertThrows(DrawingException.class,
                    () -> parser.parse("C 20 -4"));
            assertTrue(ex.getMessage().contains("positive"));
        }

        @Test
        void rejectsNegativeCoordinates() {
            assertThrows(DrawingException.class, () -> parser.parse("L -1 2 3 2"));
        }
    }
}
