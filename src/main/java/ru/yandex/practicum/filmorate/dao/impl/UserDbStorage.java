package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

/**
 * DAO класс для работы с БД пользователей
 */
@Repository
@Qualifier("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Метод добавления в БД нового пользователя
     * @param user - объект User
     * @return - добавленный пользователь
     */
    @Override
    public User createUser(User user) {

        checkValidationUser(user);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        int idUser = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        user.setId(idUser);
        log.info("Добавлен пользователь с id {}", idUser);
        return user;
    }

    /**
     * Метод удаления пользователя из БД
     * @param id - id пользователя
     */
    @Override
    public void removeUser(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from users where id = ?", id);
        if (userRows.next()) {
            String sql = "delete from users where id = ?";
            jdbcTemplate.update(sql, id);
            log.info("Удален пользователь с id {}", id);
        } else {
            log.error("Передан некорректный id пользователя: {}", id);
            throw new UserNotFoundException(String.format("Пользователь с id %d не существует.", id));
        }
    }

    /**
     * Метод обновления пользователя
     * @param updateUser - обновляемый пользователь
     * @return - обновленный порльзователь
     */
    @Override
    public User updateUser(User updateUser) {

        checkValidationUser(updateUser);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from users where id = ?", updateUser.getId());
        if (userRows.next()) {
            String sql = "update users set " +
                    "name = ?, email = ?, login = ?, birthday = ? " +
                    "where id = ?";
            jdbcTemplate.update(sql
                    , updateUser.getName()
                    , updateUser.getEmail()
                    , updateUser.getLogin()
                    , updateUser.getBirthday()
                    , updateUser.getId());
            log.info("Обновлен пользователь: {}", updateUser);
        } else {
            log.error("Передан некорректный id пользователя: {}", updateUser.getId());
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", updateUser.getId()));
        }
        return updateUser;
    }

    /**
     * Метод получения всех пользователей, хранящихся в БД
     * @return - коллекция пользователей
     */
    @Override
    public Collection<User> getAllUsers() {

        String sql = "select * from users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    /**
     * Метод получения конкретного пользователя по id
     * @param id - id пользователя
     * @return - полученный пользователь
     */
    @Override
    public User findUserById(int id) {

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from users where id = ?", id);
        if (userRows.next()) {
            String sql = "select * from users where id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                    makeUser(rs));
        } else {
            log.error("Передан некорректный id пользователя: {}", id);
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    /**
     * Метод инициализации полей пользователя на основе БД
     * @param rs переменная класса ResultSet
     * @return объект User
     * @throws SQLException
     */
    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User(rs.getString("email")
                , rs.getString("login")
                , rs.getDate("birthday").toLocalDate());
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        return user;
    }

    /**
     * Метод проверки пользователя на недопустимость определенных параметров
     * @param user - объект User
     * @throws ValidationException
     */
    private void checkValidationUser(User user) throws ValidationException {

        if (user == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationException("Тело запроса не может быть пустым.");
        }
        if (StringUtils.containsNone(user.getEmail(), "@")) {
            log.error("Передан некорректный адрес электронной почты: {}", user.getEmail());
            throw new ValidationException("Адрес электронной почты не может быть пустым и должен содержать " +
                    "символ @.");
        }
        //Не смог подобрать метод, объединяющий проверку на пустую строку и наличие пробелов, взаимоисключают результат
        if (StringUtils.isBlank(user.getLogin()) || StringUtils.containsWhitespace(user.getLogin())) {
            log.error("Передан некорректный логин: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Передана некорректная дата рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
    }
}
