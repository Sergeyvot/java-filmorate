package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

/**
 * Сервисный класс для работы с жанрами
 */
@Service
public class GenreService {

    private final GenreDaoImpl genreDao;

    public GenreService(GenreDaoImpl genreDao) {
        this.genreDao = genreDao;
    }

    /**
     * Метод получения всех жанров, хранящихся в БД
     * @return - коллекция жанров
     */
    public Collection<Genre> findAllGenre() {
        return genreDao.findAllGenre();
    }

    /**
     * Метод получения конкретного жанра по id
     * @param id - id жанра
     * @return - полученный жанр
     */
    public Genre findGenreById(int id) {
        return genreDao.findGenreById(id);
    }
}
