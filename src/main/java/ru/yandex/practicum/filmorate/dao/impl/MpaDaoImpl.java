package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

/**
 * DAO класс для работы с БД рейтингов фильма
 */
@Repository
@Slf4j
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;
    private final MpaMapper mpaMapper;

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate, MpaMapper mpaMapper) {

        this.jdbcTemplate = jdbcTemplate;
        this.mpaMapper = mpaMapper;
    }

    /**
     * Метод получения всех рейтингов, хранящихся в БД
     * @return - коллекция рейтингов
     */
    @Override
    public Collection<Mpa> findAllMpa() {
        String sql = "select * from mpa";
        return jdbcTemplate.query(sql, mpaMapper);
    }

    /**
     * Метод получения конкретного рейтинга по id
     * @param id - id рейтинга
     * @return - полученный рейтинг
     */
    @Override
    public Mpa findMpaById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from mpa where id = ?", id);

        if (userRows.next()) {
            Mpa mpa = new Mpa(
                    userRows.getInt("id"),
            userRows.getString("name"));
            log.info("Найден рейтинг: {}", mpa.getName());
            return mpa;
        } else {
            log.error("Передан некорректный id рейтинга: {}", id);
            throw new MpaNotFoundException(String.format("Рейтинг с id " + id + " не найден"));
        }
    }
}
