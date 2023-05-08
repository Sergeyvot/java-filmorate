package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс сервисного слоя для работы с фильмами
 */
public interface FilmService {

    Collection<Film> getAllFilms();
    Film findFilmById(int id);
    Film addFilm(Film film);
    void removeFilm(int id);
    Film updateFilm(Film updateFilm);
    Film addLike(int id, int userId);
    Film removeLike(int id, int userId);
    List<Film> findPopularFilms(Integer count);
}
