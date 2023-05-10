package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

/**
 * Интерфейс DAO класса GenreDaoImpl
 */
public interface GenreDao {

    Collection<Genre> findAllGenre();

    Genre findGenreById(int id);
}
