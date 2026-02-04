package drawing.command;

import drawing.context.DrawingContext;

/** Displays usage. Uses text block (Java 15+). modifiesCanvas=false. */
public record HelpCommand() implements Command {

    private static final String HELP_TEXT = """
            Commands:
              C w h           Create canvas (width x height)
              L x1 y1 x2 y2   Draw line (horizontal or vertical)
              R x1 y1 x2 y2   Draw rectangle
              B x y c         Bucket fill at (x,y) with color c
              U               Undo last action
              Z               Redo last undone action
              S <file>        Save canvas to file
              H               Show this help
              Q               Quit
            """;

    @Override
    public void execute(DrawingContext context) {
        context.getConsole().print(HELP_TEXT);
    }

    @Override
    public boolean modifiesCanvas() {
        return false;
    }
}
