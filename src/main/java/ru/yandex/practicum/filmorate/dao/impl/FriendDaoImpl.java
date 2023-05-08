package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO класс для работы с БД, определяющих друзей пользователя
 */
@Repository
@Slf4j
public class FriendDaoImpl implements FriendDao {

    private final JdbcTemplate jdbcTemplate;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public FriendDaoImpl(JdbcTemplate jdbcTemplate, UserDbStorage userStorage) {

        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    /**
     * Метод добавления друга
     * @param id - id пользователя, которому приходит запрос в друзья
     * @param friendId - id пользователя, отправляющего запрос в друзья
     * @return - пользователь, список друзей которого пополнился
     */
    @Override
    public User addFriend(int id, int friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from friends where user_id = ? AND friend_id = ?",
                id, friendId);
        if (userRows.next()) {
            log.error("Пользователь с id {} уже есть в списке друзей пользователя с id {}.", friendId, id);
            throw new ValidationException("Пользователь с id " + friendId + " уже есть в списке друзей пользователя с id "
            + id + ".");
        }
        String sqlQuery = "insert into friends(user_id, friend_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
        User user = userStorage.findUserById(id);
        user.getFriends().add(friendId);

        return user;
    }

    /**
     * Метод удаления из друзей
     * @param id - id пользователя, которому отправлен запрос на удаление из друзей
     * @param friendId - id пользователя, который отправил запрос на удаление из друзей
     * @return - пользователь, список друзей которого уменьшился
     */
    @Override
    public User removeFriend(int id, int friendId) {

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from friends where user_id = ? AND friend_id = ?",
                id, friendId);
        if (userRows.next()) {
            String sqlQuery = "delete from friends where user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlQuery, id, friendId);
        } else {
            log.error("Пользователя с id {} нет в списке друзей пользователя с id {}.", friendId, id);
            throw new ValidationException("Пользователя с id " + friendId + " нет в списке друзей пользователя с id "
                    + id + ".");
        }
        User user = userStorage.findUserById(id);
        user.getFriends().remove(friendId);

        return user;
    }

    /**
     * Метод получения списка друзей пользователя с конкретным id
     * @param id - id пользователя
     * @return - список друзей
     */
    @Override
    public List<User> findAllFriends(int id) {

        String sql = "select * from users WHERE id IN "
                +"(select friend_id from friends where user_id = ?)";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                makeUser(rs), id);
    }

    /**
     * Метод получения списка общих друзей двух пользователей
     * @param id - id первого пользователя
     * @param otherId - id второго пользователя
     * @return - список общих друзей
     */
    @Override
    public List<User> findMutualFriends(int id, int otherId) {

        String sql = "select * from users where id IN "
                + "(select friend_id from friends where user_id = ?"
                + " and friend_id in (select friend_id from friends where user_id = ?))";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                makeUser(rs),id,otherId);
    }

    /**
     * Метод инициализации полей пользователя на основе полей таблицы users
     * @param rs - переменная класса ResultSet
     * @return - пользователь
     * @throws SQLException
     */
    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User(rs.getString("email"),
                rs.getString("login"),
                rs.getDate("birthday").toLocalDate());
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        return user;
    }
}
