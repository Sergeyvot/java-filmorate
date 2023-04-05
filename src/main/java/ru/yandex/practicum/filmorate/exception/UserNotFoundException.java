package ru.yandex.practicum.filmorate.exception;

/**
 * Класс исключения, когда не найден экземпляр User
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String s) {
        super(s);
    }
}
