package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAO класс для работы с БД фильмов
 */
@Repository
@Qualifier("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDaoImpl mpaDao;
    private final GenreDaoImpl genreDao;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDaoImpl mpaDao, GenreDaoImpl genreDao) {

        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
    }

    /**
     * Метод добавления нового фильма
     * @param film - объект Film
     * @return - созданный фильм с проинициализированными полями, добавленный в БД
     */
    @Override
    public Film addFilm(Film film) {

        checkValidationFilm(film);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        int idFilm = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.setId(idFilm);
        film.setMpa(mpaDao.findMpaById(film.getMpa().getId()));

        if (!film.getGenres().isEmpty()) {
            String sqlQuery = "insert into genre_film(film_id, genre_id) " +
                    "values (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlQuery,
                        film.getId(), genre.getId());
            }
            film.setGenres(film.getGenres().stream()
                    .map(g -> g.getId())
                    .map(i -> genreDao.findGenreById(i))
                    .collect(Collectors.toSet()));
        }
        log.info("Добавлен фильм с id {}", idFilm);
        return film;
    }

    /**
     * Метод удаления фильма из БД
     * @param id - id удаляемого фильма
     */
    @Override
    public void removeFilm(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from films where id = ?", id);
        if (userRows.next()) {
            String sql = "delete from films where id = ?";
            jdbcTemplate.update(sql, id);
            log.info("Удален фильм с id {}", id);
        } else {
            log.error("Передан некорректный id фильма: {}", id);
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", id));
        }
    }

    /**
     * Метод обновления фильма в БД
     * @param updateFilm - обновляемый фильм
     * @return - обновленный фильм
     */
    @Override
    public Film updateFilm(Film updateFilm) {
        checkValidationFilm(updateFilm);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from films where id = ?", updateFilm.getId());
        if (userRows.next()) {
            String sql = "update films set " +
                    "name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                    "where id = ?";
            jdbcTemplate.update(sql,
                    updateFilm.getName(),
                    updateFilm.getDescription(),
                    updateFilm.getReleaseDate(),
                    updateFilm.getDuration(),
                    updateFilm.getMpa().getId(),
                    updateFilm.getId());
            updateFilm.setMpa(mpaDao.findMpaById(updateFilm.getMpa().getId()));

            String sqlDropGenre = "delete from genre_film where film_id = ?";
            jdbcTemplate.update(sqlDropGenre, updateFilm.getId());

            if (!updateFilm.getGenres().isEmpty()) {

                String sqlGenre = "insert into genre_film(film_id, genre_id) "
                        + "values (?, ?)";
                for (Genre genre : updateFilm.getGenres()) {
                    jdbcTemplate.update(sqlGenre, updateFilm.getId(), genre.getId());

                }
                updateFilm.setGenres(updateFilm.getGenres().stream()
                        .map(g -> g.getId())
                        .map(i -> genreDao.findGenreById(i))
                        .collect(Collectors.toSet()));

                System.out.println(updateFilm);
            }
            log.info("Обновлен фильм: {}", updateFilm);
        } else {
            log.error("Передан некорректный id фильма: {}", updateFilm.getId());
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", updateFilm.getId()));
        }
        return updateFilm;
    }

    /**
     * Метод получения всех фильмов из БД
     * @return - коллекция фильмов
     */
    @Override
    public Collection<Film> getAllFilms() {
        String sql = "select * from films";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                makeFilm(rs));
    }

    /**
     * Метод получения из БД конкретного филма по id
     * @param id - id получаемого фильма
     * @return - полученный фильм
     */
    @Override
    public Film findFilmById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from films where id = ?", id);
        if (userRows.next()) {
            String sql = "select * from films where id = ?";
            Film film = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                    makeFilm(rs));
            return film;
        } else {
            log.error("Передан некорректный id фильма: {}", id);
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", id));
        }
    }

    /**
     * Метод инициализации полей фильма на основе БД
     * @param rs
     * @return
     * @throws SQLException
     */
    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film(rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"));
        film.setId(rs.getInt("id"));
        film.setMpa(mpaDao.findMpaById(rs.getInt("mpa_id")));
        film.setGenres(getGenres(film.getId()));
        return film;
    }

    /**
     * Метод инициализации поля genres фильма с конкретным id на основе БД
     * @param id - id фильма
     * @return - Set<Genre>
     */
    private Set<Genre> getGenres(int id) {
        String sqlGenre = "select genre_id from genre_film where film_id = ?";
        return jdbcTemplate.query(sqlGenre, (rs, rowNum) ->
                        rs.getInt("genre_id"), id).stream()
                .map(i -> genreDao.findGenreById(i))
                .collect(Collectors.toSet());
    }

    /**
     * Метод проверки фильма на недопустимость определенных параметров
     * @param film - объект Film
     * @throws ValidationException
     */
    private void checkValidationFilm(Film film) throws ValidationException {

        if (film == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationException("Тело запроса не может быть пустым.");
        }
        if (StringUtils.isBlank(film.getName())) {
            log.error("Передано пустое название фильма.");
            throw new ValidationException("Название фильиа не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.error("Описание фильма больше допустимого количества символов: {}", film.getDescription().length());
            throw new ValidationException("Описание фильиа должно содержать нее более 200 символов.");
        }
        if (film.getDuration() <= 0) {
            log.error("Передана некорректная продолжительность фильма: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильиа должна быть положительной.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Передана некорректная дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть ранее 28.12.1895г.");
        }
    }
}
