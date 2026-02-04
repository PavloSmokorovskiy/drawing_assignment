package drawing.io;

/**
 * Test Double: Fake. Captures output for assertions.
 */
public class TestConsole implements Console {

    private final StringBuilder output = new StringBuilder();

    @Override
    public void print(String message) {
        output.append(message);
    }

    @Override
    public void println(String message) {
        output.append(message).append("\n");
    }

    @Override
    public void printError(String message) {
        output.append(message).append("\n");
    }

    public String getOutput() {
        return output.toString();
    }
}
