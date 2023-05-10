package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.FriendDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

/**
 * Сервисный класс для работы с БД пользователей
 */
@Service
@Slf4j
public class DbUserService implements UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private final FriendDaoImpl friendDao;

    @Autowired
    public DbUserService(UserDbStorage userStorage, JdbcTemplate jdbcTemplate, FriendDaoImpl friendDao) {

        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
        this.friendDao = friendDao;
    }

    /**
     * Метод получения всех пользователей из БД
     * @return - коллекция пользователей
     */
    @Override
    public Collection<User> findAllUsers() {
        return userStorage.getAllUsers();
    }

    /**
     * Метод получения конкретного пользователя по id
     * @param id - id пользователя
     * @return - полученный объект User
     */
    @Override
    public User findUserById(long id) {
        return userStorage.findUserById(id);
    }

    /**
     * Метод добавления нового пользователя
     * @param user - объект User
     * @return - добавленный пользователь
     */
    @Override
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    /**
     * Метод обновления пользователя
     * @param user - обновляемый объект User
     * @return - обновленный пользователь
     */
    @Override
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    /**
     * Метод добавления в друзья
     * @param id - id пользователя, которому делается запрос на добавление в друзья
     * @param friendId - id пользователя, который делает запрос на добавление в друзья
     * @return - пользователь, у которого увеличивается список друзей
     */
    @Override
    public User addFriend(long id, long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from users where id = ?", id);
        if (userRows.next()) {
            SqlRowSet userRows1 = jdbcTemplate.queryForRowSet("select 1 from users where id = ?", friendId);
            if (userRows1.next()) {
                log.info("Пользователь с id {} теперь в списке друзей пользователя с id {}", friendId, id);
                return friendDao.addFriend(id, friendId);
            } else {
                log.error("Передан некорректный id пользователя: {}", friendId);
                throw new UserNotFoundException("Пользователя с id " + friendId + " не существует.");
            }
        } else {
            log.error("Передан некорректный id пользователя: {}", id);
            throw new UserNotFoundException("Пользователя с id " + id + " не существует.");
        }
    }

    /**
     * Метод удаления из списка друзей
     * @param id - id пользователя, которому направлен запрос на удаление из списка друзей
     * @param friendId - id пользователя, который направил запрос на удаление из списка друзей
     * @return - пользователь, у которого уменьшается список друзей
     */
    @Override
    public User removeFriend(long id, long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from users where id = ?", id);
        if (userRows.next()) {
            SqlRowSet userRows1 = jdbcTemplate.queryForRowSet("select 1 from users where id = ?", friendId);
            if (userRows1.next()) {
                log.info("Пользователь с id {} удален из списка друзей пользователя с id {}", friendId, id);
                return friendDao.removeFriend(id, friendId);
            } else {
                log.error("Передан некорректный id пользователя: {}", friendId);
                throw new UserNotFoundException("Пользователя с id " + friendId + " не существует.");
            }
        } else {
            log.error("Передан некорректный id пользователя: {}", id);
            throw new UserNotFoundException("Пользователя с id " + id + " не существует.");
        }
    }

    /**
     * Метод получения списка друзей конкретного пользователя
     * @param id - id пользователя
     * @return - список друзей
     */
    @Override
    public List<User> findAllFriends(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from users where id = ?", id);
        if (userRows.next()) {
            List<User> friendsUser = friendDao.findAllFriends(id);
            log.info("У пользователя с id {} в списке " + friendsUser.size() + " друзей.", id);
            return friendDao.findAllFriends(id);
        } else {
            log.error("Передан некорректный id пользователя: {}", id);
            throw new UserNotFoundException("Пользователя с id " + id + " не существует.");
        }
    }

    /**
     * Метод получения списка общих друзей двух пользователей
     * @param id - id первого пользователя
     * @param otherId - id второго пользователя
     * @return - список общих друзей
     */
    @Override
    public List<User> findMutualFriends(long id, long otherId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select 1 from users where id = ?", id);
        if (userRows.next()) {
            SqlRowSet userRows1 = jdbcTemplate.queryForRowSet("select 1 from users where id = ?", otherId);
            if (userRows1.next()) {
                List<User> mutualFriends = friendDao.findMutualFriends(id, otherId);
                log.info("У пользователей с id {} и {} " + mutualFriends.size() + " общих друзей.", id, otherId);
                return mutualFriends;
            } else {
                log.error("Передан некорректный id пользователя: {}", otherId);
                throw new UserNotFoundException("Пользователя с id " + otherId + " не существует.");
            }
        } else {
            log.error("Передан некорректный id пользователя: {}", id);
            throw new UserNotFoundException("Пользователя с id " + id + " не существует.");
        }
    }
}
