package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

/**
 * DAO класс для работы с БД жанров фильма
 */
@Repository
@Slf4j
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper genreMapper;
    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate,GenreMapper genreMapper){

        this.jdbcTemplate=jdbcTemplate;
        this.genreMapper=genreMapper;
    }

    /**
     * Метод получения всех жанров
     * @return - коллекция жанров, хранящихся в БД
     */
    @Override
    public Collection<Genre> findAllGenre() {
        String sql = "select * from genre";
        return jdbcTemplate.query(sql, genreMapper);
    }

    /**
     * Метод получения конкретного жанра по id
     * @param id - id жанра
     * @return - полученный жанр
     */
    @Override
    public Genre findGenreById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from genre where id = ?", id);

        if(userRows.next()) {
            Genre genre = new Genre(
                    userRows.getInt("id"),
                    userRows.getString("name"));
            log.info("Найден жанр: {}", genre.getName());
            return genre;
        } else {
            log.error("Передан некорректный id жанра: {}", id);
            throw new GenreNotFoundException(String.format("Жанр с id %d не найден", id));
        }
    }
}
