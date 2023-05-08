package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс маппер для получения объекта Genre на основе БД
 */
@Component
public class GenreMapper  implements RowMapper<Genre> {

    @Override
    public Genre mapRow(ResultSet rs, int i) throws SQLException {

        return new Genre(rs.getInt("id"),
                rs.getString("name"));
    }
}
