package drawing.io;

public class TestConsole implements Console {

    private final StringBuilder output = new StringBuilder();
    private final StringBuilder errors = new StringBuilder();

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
        errors.append(message).append("\n");
    }

    public String getOutput() {
        return output.toString();
    }

    public String getErrors() {
        return errors.toString();
    }

    public void clear() {
        output.setLength(0);
        errors.setLength(0);
    }
}
