package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikeDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Like;

/**
 * DAO класс для работы с БД, определяющей лайки фильмам
 */
@Repository
@Slf4j
public class LikeDaoImpl implements LikeDao {

    private final JdbcTemplate jdbcTemplate;

    public LikeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Метод добавления лайка
     * @param id - id фильма
     * @param userId - id пользователя, проставляющего лайк
     * @return - полученный лайк
     */
    @Override
    public Like addLike(int id, int userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from likes where film_id = ? and user_id = ?",
                id, userId);
        if (userRows.next()) {
            log.error("Лайк фильму с id {} от пользователя с id {} уже существует.", id, userId);
            throw new ValidationException("Данный пользователь уже ставил лайк данному фильму.");
        }
        String sqlQuery = "insert into likes(film_id, user_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                id, userId);
        return new Like(id, userId);
    }

    /**
     * Метод удаления лайка
     * @param id - id фильма
     * @param userId - id пользователя, удаляющего лайк
     */
    @Override
    public void removeLike(int id, int userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from likes where film_id = ? and user_id = ?",
                id, userId);
        if (userRows.next()) {
            String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
            jdbcTemplate.update(sqlQuery, id, userId);
        } else {
            log.error("Пользователь с id {} не ставил лайк данному фильму", userId);
            throw new ValidationException("Пользователь с id " + userId + " не ставил лайк данному фильму.");
        }
    }
}
