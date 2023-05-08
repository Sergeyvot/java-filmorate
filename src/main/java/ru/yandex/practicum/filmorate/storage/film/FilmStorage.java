package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film addFilm(Film film);

    void removeFilm(int id);

    Film updateFilm(Film updateFilm);

    Collection<Film> getAllFilms();

    Film findFilmById(int id);
}
