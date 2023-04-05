package ru.yandex.practicum.filmorate.exception;

/**
 * Класс исключения, когда не найден экземпляр Film
 */
public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(String s) {
        super(s);
    }
}
