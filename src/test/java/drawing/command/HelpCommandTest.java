package drawing.command;

import drawing.context.DrawingContext;
import drawing.io.TestConsole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelpCommandTest {

    @Test
    void printsHelpText() {
        var console = new TestConsole();
        var context = new DrawingContext(console);

        new HelpCommand().execute(context);

        String result = console.getOutput();
        assertTrue(result.contains("Commands:"));
        assertTrue(result.contains("C w h"));
        assertTrue(result.contains("L x1 y1 x2 y2"));
        assertTrue(result.contains("B x y c"));
        assertTrue(result.contains("U"));
        assertTrue(result.contains("Z"));
        assertTrue(result.contains("Q"));
    }

    @Test
    void doesNotModifyCanvas() {
        assertFalse(new HelpCommand().modifiesCanvas());
    }

    @Test
    void doesNotQuit() {
        assertFalse(new HelpCommand().shouldQuit());
    }
}
