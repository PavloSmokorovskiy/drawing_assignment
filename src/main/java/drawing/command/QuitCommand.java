package drawing.command;

import drawing.context.DrawingContext;

/**
 * Команда выхода из приложения.
 *
 * ПОЧЕМУ ОТДЕЛЬНЫЙ КЛАСС, А НЕ ОСОБЫЙ СЛУЧАЙ В ПАРСЕРЕ?
 *
 * Можно было бы в парсере возвращать null для "Q" и проверять в главном цикле.
 * Но это нарушило бы единообразие: все остальные команды — объекты.
 *
 * С QuitCommand:
 * - Паттерн Command соблюдается для всех действий
 * - Можно добавить логику в execute() (например, запрос подтверждения)
 * - Легко тестировать: создаём QuitCommand и проверяем shouldQuit()
 *
 * ПОЧЕМУ execute() ПУСТОЙ?
 *
 * Потому что выход обрабатывается в DrawingApp.run() через shouldQuit().
 * Когда команда возвращает shouldQuit() == true, цикл завершается.
 *
 * execute() существует, но ничего не делает. Можно было бы бросить исключение,
 * но это усложнило бы код. Пустой метод — простое и понятное решение.
 */
public record QuitCommand() implements Command {

    @Override
    public void execute(DrawingContext ctx) {
        // Ничего не делаем — выход обрабатывается через shouldQuit()
    }

    @Override
    public boolean shouldQuit() {
        return true;
    }

    @Override
    public boolean modifiesCanvas() {
        return false;
    }
}
