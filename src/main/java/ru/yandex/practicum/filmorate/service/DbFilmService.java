package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.LikeDaoImpl;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервисный класс для работы с БД фильмов
 */
@Service
@Slf4j
public class DbFilmService implements FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final LikeDaoImpl likeDao;
    private final GenreDaoImpl genreDao;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbFilmService(FilmDbStorage filmStorage, LikeDaoImpl likeDao,
                         GenreDaoImpl genreDao, JdbcTemplate jdbcTemplate) {

        this.filmStorage = filmStorage;
        this.likeDao = likeDao;
        this.genreDao = genreDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Метод для получения всех фильмов, хранящихся в БД
     * @return - коллекция фильмов
     */
    @Override
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    /**
     * Метод для получения конкретного фильма по id
     * @param id - id фильма
     * @return - полученный фильм
     */
    @Override
    public Film findFilmById(long id) {

        return filmStorage.findFilmById(id);
    }

    /**
     * Метод для добавления фильма
     * @param film - добавляемый объект Film
     * @return - добавленный фильм
     */
    @Override
    public Film addFilm(Film film) {

        return filmStorage.addFilm(film);
    }

    /**
     * Метод для удаления фильма из БД
     * @param id - id фильма
     */
    @Override
    public void removeFilm(long id) {
        filmStorage.removeFilm(id);
    }

    /**
     * Метод для обновления фильма
     * @param updateFilm - обновляемый объект Film
     * @return - обновленный фильм
     */
    @Override
    public Film updateFilm(Film updateFilm) {

        return filmStorage.updateFilm(updateFilm);
    }

    /**
     * Метод для добавления фильму лайка
     * @param id - id фильма
     * @param userId - id пользователя, который ставит лайк
     * @return - фильм, которому выставлен лайк
     */
    @Override
    public Film addLike(long id, long userId) {

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from films where id = ?", id);
        if (userRows.next()) {
            SqlRowSet userRows1 = jdbcTemplate.queryForRowSet("select 1 from users where id = ?", userId);
            if (userRows1.next()) {
                Like like = likeDao.addLike(id, userId);
                Film film = filmStorage.findFilmById(id);
                if (like != null) {
                    film.getLikes().add(userId);
                    log.info("Добавлен лайк от пользователя с id {}", userId);
                }
                return film;
            } else {
                log.error("Передан некорректный id пользователя: {}", userId);
                throw new UserNotFoundException("Пользователь с id " + userId + " не существует.");
            }
        } else {
            log.error("Передан некорректный id фильма: {}", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не существует.");
        }
    }

    /**
     * Метод для удаления лайка
     * @param id - id фильма
     * @param userId - id пользователя, который удаляет свой лайк
     * @return - фильм, у которого удален лайк
     */
    @Override
    public Film removeLike(long id, long userId) {

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from films where id = ?", id);
        if (userRows.next()) {
            SqlRowSet userRows1 = jdbcTemplate.queryForRowSet("select 1 from users where id = ?", userId);
            if (userRows1.next()) {
                likeDao.removeLike(id, userId);
                Film film = filmStorage.findFilmById(id);
                film.getLikes().remove(userId);
                log.info("Удален лайк от пользователя с id {}", userId);
                return film;
            } else {
                log.error("Передан некорректный id пользователя: {}", userId);
                throw new UserNotFoundException("Пользователь с id " + userId + " не существует.");
            }
        } else {
            log.error("Передан некорректный id фильма: {}", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не существует.");
        }
    }

    /**
     * Метод получения списка фильмов по популярности (количеству лайков)
     * @param count - лимит списка
     * @return - список фильмов по популярности
     */
    @Override
    public List<Film> findPopularFilms(Integer count) {

        String sql = "select f.*, mpa.name as mpa_name from films f "
        + "join mpa on f.mpa_id = mpa.id "
        + "right join (select film_id, count(user_id) from likes group by film_id order by count(user_id) desc limit "
                + count + ") as popular on f.id = popular.film_id";

        List<Film> popularFilms = jdbcTemplate.query(sql, (rs, rowNum) ->
                makeFilm(rs));
        if (popularFilms.isEmpty()) {
            return filmStorage.getAllFilms().stream().collect(Collectors.toList());
        }
        return popularFilms;
    }

    /**
     * Метод формирования объекта Film по значениям таблицы films
     * @param rs - переменная класса ResultSet
     * @return - объект Film
     * @throws SQLException
     */
    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film(rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"));
        film.setId(rs.getLong("id"));
        film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
        film.setGenres(getGenres(film.getId()));
        return film;
    }

    /**
     * Метод получения списка жанров по id фильма
     * @param id - id фильма
     * @return - список жанров
     */
    private Set<Genre> getGenres(long id) {
        String sqlGenre = "select genre_id from genre_film where film_id = ?";
        return jdbcTemplate.query(sqlGenre, (rs, rowNum) ->
                        rs.getInt("genre_id"), id).stream()
                .map(i -> genreDao.findGenreById(i))
                .collect(Collectors.toSet());
    }
}
