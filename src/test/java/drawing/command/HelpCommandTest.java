package drawing.command;

import drawing.context.DrawingContext;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class HelpCommandTest {

    @Test
    void printsHelpText() {
        var output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        var command = new HelpCommand();
        command.execute(new DrawingContext());

        String result = output.toString();
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
