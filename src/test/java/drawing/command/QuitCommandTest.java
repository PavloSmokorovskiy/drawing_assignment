package drawing.command;

import drawing.context.DrawingContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests QuitCommand. Verifies shouldQuit() flag for REPL termination.
 */
class QuitCommandTest {

    @Test
    void shouldQuit() {
        assertTrue(new QuitCommand().shouldQuit());
    }

    @Test
    void doesNotModifyCanvas() {
        assertFalse(new QuitCommand().modifiesCanvas());
    }

    @Test
    void executeDoesNothing() {
        var context = new DrawingContext();
        var command = new QuitCommand();

        assertDoesNotThrow(() -> command.execute(context));
        assertNull(context.getCanvas());
    }
}
