package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

/**
 * Интерфейс для методов добавления, удаления и модификации объектов Film
 */
public interface FilmStorage {
    Film addFilm(Film film);

    void removeFilm(Long id);

    Film updateFilm(Film updateFilm);

    Map<Long, Film> getFilms();

    Collection<Film> getAllFilms();

    Film findFilmById(Long id);
}
