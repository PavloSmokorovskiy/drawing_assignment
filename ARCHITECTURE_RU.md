# Архитектура проекта

## Общая идея

Это консольное приложение для рисования, построенное по принципам чистой архитектуры с использованием современных возможностей Java 21.

Когда я проектировал это приложение, я ставил перед собой несколько целей:
- **Тестируемость** — каждый компонент можно протестировать изолированно
- **Расширяемость** — добавление новых команд не требует изменения существующего кода
- **Простота** — никакого over-engineering, только то, что реально нужно

---

## Паттерны проектирования

### Command Pattern

Каждое действие пользователя инкапсулировано в объект-команду:

```
Ввод пользователя → Parser → Command → Execute → Canvas
```

#### Что это за паттерн и зачем он нужен в индустрии?

**Command Pattern** (Gang of Four) — это поведенческий паттерн, который превращает запрос в самостоятельный объект. Вместо вызова метода напрямую мы создаём объект, описывающий "что нужно сделать".

**Где применяется в реальных системах:**

| Область | Пример использования |
|---------|---------------------|
| **GUI-фреймворки** | Кнопки, меню, горячие клавиши — все привязаны к Command-объектам. Один и тот же Command может вызываться из меню, тулбара и по Ctrl+S |
| **Транзакционные системы** | Банковские операции как объекты: можно логировать, откатывать, повторять |
| **Message Queues** | RabbitMQ, Kafka — сообщения это фактически сериализованные команды |
| **CQRS** | Command Query Responsibility Segregation — архитектурный паттерн, где команды (изменения) отделены от запросов (чтение) |
| **Игровые движки** | Действия игрока как команды — позволяет делать replay, undo, сетевую синхронизацию |
| **CI/CD пайплайны** | Jenkins, GitLab CI — каждый шаг это команда с execute() |

**Почему я выбрал этот паттерн:**

1. **Единообразие** — все команды имеют один интерфейс `execute(DrawingContext)`
2. **Расширяемость** — добавить новую команду = создать новый класс, не трогая старый код (Open/Closed Principle)
3. **Тестируемость** — каждую команду можно тестировать изолированно
4. **Sealed interface** — компилятор проверяет полноту switch-выражений

**Альтернатива, которую я отверг:**

```java
// Большой switch в главном цикле — плохо!
switch(commandType) {
    case "L": drawLine(...); break;
    case "R": drawRect(...); break;
    // ... всё в одном месте
}
```

Проблемы: один огромный класс, сложно тестировать, нарушает Single Responsibility Principle.

---

### Memento Pattern

Состояние холста сохраняется в `CanvasMemento` для undo/redo:

```
Действие → Сохранить состояние → Выполнить → [Undo] → Восстановить состояние
```

#### Что это за паттерн и зачем он нужен в индустрии?

**Memento Pattern** (Gang of Four) — позволяет сохранять и восстанавливать предыдущее состояние объекта без раскрытия деталей его реализации.

**Три роли в паттерне:**
- **Originator** (Canvas) — объект, состояние которого сохраняем
- **Memento** (CanvasMemento) — "снимок" состояния, непрозрачный для внешнего кода
- **Caretaker** (CommandHistory) — хранит снимки, не зная их внутреннюю структуру

**Где применяется в реальных системах:**

| Область | Пример использования |
|---------|---------------------|
| **Текстовые редакторы** | Ctrl+Z в Word, VS Code — каждое состояние документа это Memento |
| **Графические редакторы** | Photoshop, Figma — история действий |
| **Игры** | Save/Load, checkpoints — снимок состояния игры |
| **Базы данных** | Снапшоты, point-in-time recovery |
| **Виртуализация** | VMware snapshots — снимок всей VM |
| **Git** | Каждый коммит — это по сути memento репозитория |
| **Event Sourcing** | Хранение всех событий позволяет восстановить любое состояние |

**Почему Memento, а не Command.undo()?**

Альтернативный подход — каждая команда знает, как себя откатить:

```java
interface Command {
    void execute(...);
    void undo(...);  // обратная операция
}
```

Я отказался от этого, потому что:

1. **Сложность отката** — как "откатить" flood fill? Нужно помнить ВСЕ изменённые пиксели. Это фактически тот же Memento.
2. **Хрупкость** — легко написать неправильный undo. Особенно для сложных операций.
3. **Единообразие** — с Memento все команды восстанавливаются одинаково.

**Trade-off:** Memento использует больше памяти (полная копия холста), но это разумная цена за простоту и надёжность. В production-системах часто используют гибрид: простые операции откатываются через inverse command, сложные — через snapshot.

---

### Strategy Pattern

Интерфейс `Console` абстрагирует вывод:

```
Command → Context → Console (интерфейс)
                      ↓
            SystemConsole | TestConsole
```

#### Что это за паттерн и зачем он нужен в индустрии?

**Strategy Pattern** (Gang of Four) — определяет семейство алгоритмов, инкапсулирует каждый из них и делает их взаимозаменяемыми. Позволяет изменять алгоритм независимо от клиентов, которые его используют.

**Ключевая идея:** "Program to an interface, not an implementation" — один из фундаментальных принципов ООП.

**Где применяется в реальных системах:**

| Область | Пример использования |
|---------|---------------------|
| **Сортировка** | Collections.sort() принимает Comparator — разные стратегии сравнения |
| **Валидация** | Разные правила валидации для разных стран/контекстов |
| **Аутентификация** | OAuth, LDAP, Basic Auth — разные стратегии, один интерфейс |
| **Кэширование** | Redis, Memcached, in-memory — стратегия выбирается при конфигурации |
| **Платёжные системы** | Stripe, PayPal, Apple Pay — PaymentStrategy interface |
| **Логирование** | SLF4J — абстракция над Logback, Log4j, и другими реализациями |
| **Компрессия** | GZIP, ZIP, LZ4 — CompressionStrategy |

**Зачем абстракция для System.out?**

Это пример **Dependency Inversion Principle** (SOLID): высокоуровневые модули не должны зависеть от низкоуровневых, оба должны зависеть от абстракций.

Альтернативы для тестирования:
- Мокировать System.out через PowerMock — сложно и хрупко
- Перенаправлять в ByteArrayOutputStream — глобальное изменение, не потокобезопасно

С Console-интерфейсом:
- TestConsole собирает вывод в StringBuilder
- Никаких mock-библиотек
- Чистые, изолированные тесты

---

### Context Object Pattern

`DrawingContext` — контейнер для всего состояния приложения.

#### Что это за паттерн и зачем он нужен в индустрии?

**Context Object** — паттерн, который инкапсулирует состояние, разделяемое между компонентами, в единый объект.

**Проблема, которую решает:**

```java
// Без Context — "длинные" списки параметров
void execute(Canvas canvas, CommandHistory history, Console console, CanvasRenderer renderer);

// С Context — чисто и расширяемо
void execute(DrawingContext context);
```

**Где применяется в реальных системах:**

| Область | Пример использования |
|---------|---------------------|
| **Web-фреймворки** | HttpServletRequest, Spring's WebRequest — контекст HTTP-запроса |
| **Android** | Context — доступ к ресурсам, сервисам, preferences |
| **GraphQL** | Context передаётся в resolvers — содержит user, dataloaders |
| **gRPC** | Context для передачи метаданных, deadlines, cancellation |
| **Тестирование** | TestContext в JUnit 5 — информация о текущем тесте |

**Преимущества:**
- Добавление новой зависимости не меняет сигнатуры методов
- Компоненты берут только то, что им нужно
- Упрощает dependency injection

---

## Принципы SOLID в проекте

### Single Responsibility Principle (SRP)

Каждый класс имеет одну причину для изменения:

| Класс | Ответственность |
|-------|-----------------|
| `Canvas` | Хранение и модификация пикселей |
| `CanvasRenderer` | Преобразование в текст с рамками |
| `CommandParser` | Разбор строки в команду |
| `CommandHistory` | Управление стеками undo/redo |
| `DrawLineCommand` | Логика рисования линии |

### Open/Closed Principle (OCP)

Система открыта для расширения, закрыта для модификации:

```java
// Добавить новую команду CircleCommand:
// 1. Создать CircleCommand implements Command
// 2. Добавить в permits список sealed interface
// 3. Добавить case в CommandParser

// НЕ нужно менять: DrawingApp, Canvas, другие команды
```

### Liskov Substitution Principle (LSP)

Любая реализация Command может использоваться везде, где ожидается Command:

```java
Command cmd = parser.parse(input);  // Может быть любая команда
cmd.execute(context);               // Работает единообразно
```

### Interface Segregation Principle (ISP)

Console имеет только необходимые методы: `print`, `println`, `printError`.
Не заставляем реализовывать `readLine()`, если это не нужно.

### Dependency Inversion Principle (DIP)

Команды зависят от абстракции Console, а не от System.out:

```java
// Плохо: жёсткая зависимость
System.out.println(message);

// Хорошо: зависимость от абстракции
context.getConsole().println(message);
```

---

## Алгоритмы

### Flood Fill (Заливка) — BFS

Использую **BFS (Breadth-First Search)** вместо рекурсии:

```
queue = ArrayDeque<Point>()
visited = HashSet<Point>()    // O(1) проверка
queue.offer(start)
visited.add(start)

while (!queue.isEmpty()):
    p = queue.poll()
    setPixel(p, color)
    // Добавляем непосещённых соседей с нужным цветом
```

#### Что такое BFS и где он применяется в индустрии?

**Breadth-First Search** — алгоритм обхода графа, который исследует всех соседей текущей вершины перед переходом к соседям соседей.

**Характеристики:**
- Использует очередь (Queue)
- Гарантирует кратчайший путь в невзвешенном графе
- Сложность: O(V + E) где V — вершины, E — рёбра

**Где применяется в реальных системах:**

| Область | Пример использования |
|---------|---------------------|
| **Социальные сети** | "Люди, которых вы можете знать" — друзья друзей (BFS от вашего профиля) |
| **GPS-навигация** | Поиск кратчайшего маршрута (упрощённо, реально используют Dijkstra/A*) |
| **Сетевое оборудование** | Протоколы маршрутизации, broadcast |
| **Веб-краулеры** | Google bot обходит страницы по ссылкам |
| **Игры** | Pathfinding в играх, проверка достижимости |
| **Графические редакторы** | Flood fill (заливка) — именно наш случай |
| **Garbage Collection** | Mark phase в mark-and-sweep GC |

**Почему не рекурсия (DFS)?**

```java
// Рекурсивный DFS — опасно!
void fill(x, y, target, color) {
    if (outOfBounds || pixel != target) return;
    setPixel(color);
    fill(x+1, y, ...);  // Рекурсия
    fill(x-1, y, ...);
    fill(x, y+1, ...);
    fill(x, y-1, ...);
}
```

**Проблема: StackOverflowError**
- Холст 1000x1000 = до миллиона рекурсивных вызовов
- Стек Java ~512KB, каждый кадр ~32 байта = ~16000 вызовов максимум
- BFS использует очередь в heap-памяти — контролируемое потребление

**Почему HashSet для visited?**

| Структура | contains() | add() | Подходит? |
|-----------|-----------|-------|-----------|
| ArrayList | O(n) | O(1) | Нет — медленный поиск |
| LinkedList | O(n) | O(1) | Нет — медленный поиск |
| HashSet | O(1) | O(1) | Да — быстрый поиск |
| TreeSet | O(log n) | O(log n) | Избыточно — не нужна сортировка |

Для flood fill на 1M пикселей: HashSet = 1M операций O(1), ArrayList = 1M × 1M = 1T операций!

---

### Defensive Copying

#### Что это и зачем нужно в индустрии?

**Defensive Copying** — техника создания копий объектов при получении или возврате, чтобы защитить внутреннее состояние от внешних модификаций.

**Проблема без defensive copying:**

```java
class Canvas {
    private char[][] pixels;

    public char[][] getPixels() {
        return pixels;  // Опасно! Внешний код может изменить наши данные
    }
}

// Клиентский код
char[][] pixels = canvas.getPixels();
pixels[0][0] = 'X';  // Изменили внутреннее состояние Canvas!
```

**Решение:**

```java
public char[][] getPixels() {
    return PixelArrays.copy(pixels);  // Возвращаем копию
}
```

**Где критически важно в реальных системах:**

| Область | Почему важно |
|---------|--------------|
| **Безопасность** | Защита от tampering — клиент не может изменить данные сервера |
| **Многопоточность** | Защита от race conditions — каждый поток работает со своей копией |
| **Кэширование** | Кэш не должен быть испорчен клиентским кодом |
| **Immutable objects** | Java String, Integer — defensive copying обеспечивает неизменяемость |
| **API design** | Effective Java (Joshua Bloch) рекомендует для всех mutable объектов |

**В нашем проекте:**
- `Canvas.copyPixels()` — для создания Memento
- `CanvasMemento.restore()` — копируем при восстановлении (один Memento может использоваться многократно при redo)

---

## Обработка ошибок

### Единое исключение vs Иерархия

#### Подходы в индустрии

**Подход 1: Иерархия исключений**
```java
DrawingException
├── InvalidCommandException
├── CanvasNotCreatedException
├── OutOfBoundsException
└── ...
```

**Когда это нужно:** когда разные типы ошибок обрабатываются по-разному.

Пример: HTTP-клиент
```java
try {
    httpClient.get(url);
} catch (ConnectionTimeoutException e) {
    retry();
} catch (AuthenticationException e) {
    refreshToken();
} catch (NotFoundException e) {
    return defaultValue;
}
```

**Подход 2: Единое исключение (наш случай)**

**Когда это подходит:** когда все ошибки обрабатываются одинаково.

В нашем приложении: показать сообщение → продолжить работу.

```java
catch (DrawingException e) {
    console.println("Error: " + e.getMessage());
    // Продолжаем работу
}
```

### Checked vs Unchecked Exceptions

#### Философия в индустрии

**Checked (Exception):**
- Компилятор требует обработки
- Засоряет сигнатуры методов
- Споры: "навязывает обработку" vs "мусор в коде"

**Unchecked (RuntimeException):**
- Не требует декларации throws
- Современный тренд: Spring, Hibernate используют RuntimeException
- Kotlin, Scala — вообще нет checked exceptions

**Почему я выбрал RuntimeException:**
1. Все ошибки ловятся в одном месте — главном цикле
2. Промежуточные методы не засоряются throws
3. Это ошибки ввода, не системные сбои

---

## Современные возможности Java

### Sealed Interfaces (Java 17)

#### Что это и зачем нужно?

**Sealed** — модификатор, ограничивающий, кто может реализовать интерфейс.

```java
public sealed interface Command permits CreateCanvasCommand, DrawLineCommand, ... {
```

**Где применяется в индустрии:**

| Сценарий | Польза |
|----------|--------|
| **Алгебраические типы данных** | Моделирование конечного набора вариантов (как enum, но с данными) |
| **Pattern Matching** | Компилятор проверяет полноту switch |
| **API Design** | Контроль расширения — только "официальные" реализации |
| **Domain Modeling** | OrderStatus: Pending \| Shipped \| Delivered \| Cancelled |

**Преимущество в нашем коде:**

```java
return switch (type) {
    case "C" -> parseCanvas(parts);
    case "L" -> parseLine(parts);
    // Если забудем case — компилятор предупредит (с sealed)
};
```

### Records (Java 16)

#### Что это и зачем нужно?

**Record** — специальный тип класса для неизменяемых данных.

```java
public record Point(int x, int y) { }
// Автоматически генерирует: конструктор, getters, equals, hashCode, toString
```

**Где применяется в индустрии:**

| Сценарий | Пример |
|----------|--------|
| **DTO** | UserDTO, OrderResponse |
| **Value Objects** | Money, Email, UserId |
| **Events** | UserCreatedEvent, OrderPlacedEvent |
| **Configuration** | DatabaseConfig, ApiSettings |
| **Tuples** | Pair<A,B>, Triple<A,B,C> |

**В нашем проекте:** Point, все Command-ы — идеальные кандидаты для records.

---

## Архитектурные решения (сводная таблица)

| Решение | Почему так | Индустриальный контекст |
|---------|------------|------------------------|
| **Command Pattern** | Расширяемость, тестируемость | GUI, Message Queues, CQRS |
| **Memento Pattern** | Надёжный undo без сложной логики | Редакторы, Git, Event Sourcing |
| **Strategy Pattern** | Тестируемость без моков | DI-контейнеры, SLF4J |
| **Sealed interface** | Compile-time safety | Pattern Matching, ADT |
| **BFS с HashSet** | O(n) вместо O(n²) | Графовые алгоритмы везде |
| **Defensive copying** | Защита состояния | Effective Java best practice |
| **Single exception** | Uniform handling | Когда все ошибки равноценны |
| **RuntimeException** | Clean method signatures | Современный Java-тренд |

---

## Транзакционность Undo

Особое внимание я уделил корректности undo при ошибках:

```java
// В DrawingApp.run()
if (command.modifiesCanvas()) {
    history.saveState(canvas);  // 1. Сохраняем состояние
}

try {
    command.execute(context);   // 2. Выполняем команду
} catch (DrawingException e) {
    if (command.modifiesCanvas()) {
        history.discardLastState();  // 3. Откатываем сохранение при ошибке
    }
    throw e;
}
```

#### Аналогия с базами данных

Это похоже на **транзакции в СУБД**:

```sql
BEGIN TRANSACTION;
-- операции
COMMIT;  -- или ROLLBACK при ошибке
```

**Свойства ACID** (упрощённо в нашем контексте):
- **Atomicity** — либо команда выполнилась полностью, либо состояние не изменилось
- **Consistency** — история всегда в корректном состоянии
- **Isolation** — N/A (однопоточное приложение)
- **Durability** — N/A (in-memory)

---

## Структура пакетов

```
drawing/
├── DrawingApp.java           # Точка входа, REPL-цикл
│
├── canvas/                   # Доменный слой
│   ├── Canvas.java           # Холст с операциями рисования
│   ├── CanvasMemento.java    # Снимок состояния для undo/redo
│   ├── CanvasRenderer.java   # Преобразует холст в текст с рамками
│   ├── Point.java            # Неизменяемая координата (x, y)
│   ├── PixelArrays.java      # Утилита для копирования массивов
│   └── DrawingConstants.java # Константы (символы, лимиты)
│
├── command/                  # Реализация Command Pattern
│   ├── Command.java          # Sealed interface — все команды
│   └── *Command.java         # Конкретные команды
│
├── context/                  # Состояние сессии
│   └── DrawingContext.java   # Контейнер: canvas, history, renderer, console
│
├── history/                  # Управление undo/redo
│   └── CommandHistory.java   # Два стека: undo и redo
│
├── io/                       # Абстракция ввода-вывода
│   ├── Console.java          # Интерфейс для вывода
│   └── SystemConsole.java    # Продакшен-реализация
│
├── parser/                   # Разбор команд
│   └── CommandParser.java    # Строка → объект Command
│
└── exception/
    └── DrawingException.java # Единственное исключение приложения
```

---

## Альтернативы, которые я рассмотрел и отверг

| Альтернатива | Почему отказался | Когда бы выбрал |
|--------------|------------------|-----------------|
| Spring DI | Overkill для CLI-приложения | Большой enterprise-проект |
| Command.undo() | Сложно откатить flood fill | Простые операции (add/remove) |
| Иерархия исключений | Все ошибки обрабатываются одинаково | Разная логика для разных ошибок |
| Хранить рамки в Canvas | Рамки — представление, не данные | Никогда |
| ArrayDeque для истории | Не поддерживает null | Когда null не нужен |
| Рекурсия для flood fill | StackOverflow | Гарантированно маленькие области |
| Мокирование System.out | Хрупкие тесты | Когда нет времени на абстракцию |
