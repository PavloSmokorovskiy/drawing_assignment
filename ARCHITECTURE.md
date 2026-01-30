# Architecture

## Overview

A console drawing application built with clean architecture principles and modern Java 21 features.

## Design Patterns

### Command Pattern

Each user action is encapsulated as a `Command` object:

```
User Input → Parser → Command → Execute → Canvas
```

**Benefits:**
- Uniform execution interface via `execute(DrawingContext)`
- Easy addition of new commands without modifying existing code
- Foundation for undo/redo functionality
- Sealed interface enables compile-time exhaustiveness checking

### Memento Pattern

Canvas state is captured in `CanvasMemento` for undo/redo:

```
Action → Save State → Execute → [Undo] → Restore State
```

**Why Memento over Command.undo()?**
- Simpler implementation: save an entire state vs. reverse each operation
- Some operations (flood fill) are expensive to reverse
- Consistent behavior regardless of command complexity

### Strategy Pattern

`Console` interface abstracts output for testability:

```
Command → Context → Console (interface)
                      ↓
            SystemConsole | TestConsole
```

## Key Design Decisions

| Decision                   | Rationale                                                 |
|----------------------------|-----------------------------------------------------------|
| **Sealed interface**       | Compiler enforces exhaustive switch expressions           |
| **Records for commands**   | Immutable, auto-generated equals/hashCode, concise        |
| **BFS with HashSet**       | O(n) flood fill vs O(n²) with naive visited list          |
| **LinkedList for stacks**  | Supports null elements (canvas before creation)           |
| **Package-private access** | `PixelArrays`, `copyPixels()` hidden from external use    |
| **Defensive copying**      | `CanvasMemento` copies pixels to prevent state corruption |

## Package Structure

```
drawing/
├── DrawingApp.java           # Application entry point, main loop
│
├── canvas/                   # Domain layer
│   ├── Canvas.java           # Drawing surface with operations
│   ├── CanvasMemento.java    # State snapshot for undo/redo
│   ├── CanvasRenderer.java   # Converts canvas to string output
│   ├── Point.java            # Immutable coordinate pair
│   ├── PixelArrays.java      # Internal array utilities
│   └── DrawingConstants.java # Shared constants
│
├── command/                  # Command pattern implementation
│   ├── Command.java          # Sealed interface
│   ├── CreateCanvasCommand   # C w h
│   ├── DrawLineCommand       # L x1 y1 x2 y2
│   ├── DrawRectangleCommand  # R x1 y1 x2 y2
│   ├── BucketFillCommand     # B x y c
│   ├── UndoCommand           # U
│   ├── RedoCommand           # Z
│   ├── SaveCommand           # S filename
│   ├── HelpCommand           # H
│   └── QuitCommand           # Q
│
├── context/                  # Session state
│   └── DrawingContext.java   # Holds canvas, history, renderer
│
├── history/                  # Undo/redo management
│   └── CommandHistory.java   # Manages undo/redo stacks
│
├── io/                       # Output abstraction
│   ├── Console.java          # Interface for output
│   └── SystemConsole.java    # Production implementation
│
├── parser/                   # Input processing
│   └── CommandParser.java    # Parses input into commands
│
└── exception/
    └── DrawingException.java # Application-specific errors
```

## Algorithm Details

### Flood Fill (Bucket Fill)

Uses **Breadth-First Search** with a `HashSet` for visited tracking:

```
queue = new ArrayDeque<Point>()
visited = new HashSet<Point>()    // O(1) contains check
queue.offer(start)
visited.add(start)

while (!queue.isEmpty()):
    p = queue.poll()
    setPixel(p, color)
    // Add unvisited neighbors with matching color
```

**Complexity:** O(n) where n = pixels to fill

**Why not recursion?** Stack overflow on large areas.

### Line Drawing

Supports horizontal and vertical lines only (per requirements). Uses `Math.min/max` to handle any direction:

```
x1 = Math.min(from.x(), to.x())
x2 = Math.max(from.x(), to.x())
// Works regardless of input order
```

## Error Handling

Single `DrawingException` for all application errors:

- Parser errors (invalid syntax, wrong argument count)
- Validation errors (out of bounds, invalid color)
- State errors (no canvas, nothing to undo)

**Why a single exception?** All errors are handled identically (display message). Hierarchy would be overengineering.

## Testability

1. **Console abstraction** — `TestConsole` captures output without mocking
2. **Pure domain logic** — Canvas operations are side-effect-free
3. **Immutable commands** — No shared mutable state
4. **Integration tests** — Full scenarios test command interactions

## Trade-offs Considered

| Alternative               | Why Not Chosen                     |
|---------------------------|------------------------------------|
| Spring DI                 | Overkill for CLI app scope         |
| Command.undo() method     | Complex to reverse flood fill      |
| Exception hierarchy       | All errors handled same way        |
| Storing borders in Canvas | Borders are presentation, not data |
| ArrayDeque for history    | Doesn't support null elements      |
