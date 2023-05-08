package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс маппер для получения объекта Mpa на основе БД
 */
@Component
public class MpaMapper implements RowMapper<Mpa> {

    @Override
    public Mpa mapRow(ResultSet rs, int i) throws SQLException {

        return new Mpa(rs.getInt("id"),
                rs.getString("name"));
    }
}
