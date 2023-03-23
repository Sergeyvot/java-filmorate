package ru.yandex.practicum.filmorate.exception;

/**
 * Класс исключения, генерируемого при некорректных условиях валидации в классах-контроллерах
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String s) {
        super(s);
    }
}
