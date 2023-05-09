package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс сервисного слоя для работы с фильмами
 */
public interface FilmService {

    Collection<Film> getAllFilms();

    Film findFilmById(long id);

    Film addFilm(Film film);

    void removeFilm(long id);

    Film updateFilm(Film updateFilm);

    Film addLike(long id, long userId);

    Film removeLike(long id, long userId);

    List<Film> findPopularFilms(Integer count);
}
