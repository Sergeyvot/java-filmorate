package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис класс, реализующий логику добавления, удаления друзей, получения списка друзей
 */
@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    /**
     * Внедрены зависимости интерфейса хранилища UserStorage
     * @param userStorage экземпляр класса InMemoryUserStorage
     */
    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    /**
     * Метод добавления в друзья
     * @param id id пользователя, которому добавляется друг
     * @param friendId id друга
     * @return пользователь, которому добавлен друг
     */
    public User addFriend(long id, long friendId) {
        if (!userStorage.getUsers().containsKey(id)) {
            log.error("Передана некорректный id пользователя: {}", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не существует.");
        }
        if (!userStorage.getUsers().containsKey(friendId)) {
            log.error("Передана некорректный id пользователя: {}", friendId);
            throw new UserNotFoundException("Пользователь с id " + friendId + " не существует.");
        }
        User user = userStorage.getUsers().get(id);
        user.getFriends().add(friendId);
        User userFriend = userStorage.getUsers().get(friendId);
        userFriend.getFriends().add(id);
        log.info("Пользователи с id " + id + " и id {} теперь друзья", friendId);

        return user;
    }

    /**
     * Метод удаления из друзей
     * @param id id пользователя, у которого удаляется друг
     * @param friendId id друга
     * @return пользователь, у которого удален друг
     */
    public User removeFriend(long id, long friendId) {
        if (!userStorage.getUsers().containsKey(id)) {
            log.error("Передана некорректный id пользователя: {}", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не существует.");
        }
        if (!userStorage.getUsers().containsKey(friendId)) {
            log.error("Передана некорректный id пользователя: {}", friendId);
            throw new UserNotFoundException("Пользователь с id " + friendId + " не существует.");
        }
        User user = userStorage.getUsers().get(id);
        user.getFriends().remove(friendId);
        User userFriend = userStorage.getUsers().get(friendId);
        userFriend.getFriends().remove(id);
        log.info("Пользователи с id " + id + " и id {} теперь не друзья", friendId);
        return user;
    }

    /**
     * Метод получения списка друзей конкретного пользователя
     * @param id id пользователя
     * @return список друзей пользователя
     */
    public List<User> findAllFriends(long id) {
        if (!userStorage.getUsers().containsKey(id)) {
            log.error("Передана некорректный id пользователя: {}", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не существует.");
        }
        User user = userStorage.getUsers().get(id);
        List<User> userFriends = new ArrayList<>();
        if (!user.getFriends().isEmpty()) {
            for (Long idFriend : user.getFriends()) {
                userFriends.add(userStorage.getUsers().get(idFriend));
            }
        }
        log.info("У пользователя с id " + id + " {} друзей", userFriends.size());
        return userFriends;
    }

    /**
     * Метод получения списка друзеей, общих для двух пользователей
     * @param id id первого пользователя
     * @param otherId id второго пользователя
     * @return список общих друзей
     */
    public List<User> findMutualFriends(long id, long otherId) {
        if (!userStorage.getUsers().containsKey(id)) {
            log.error("Передана некорректный id пользователя: {}", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не существует.");
        }
        if (!userStorage.getUsers().containsKey(otherId)) {
            log.error("Передана некорректный id пользователя: {}", otherId);
            throw new UserNotFoundException("Пользователь с id " + otherId + " не существует.");
        }
        User user = userStorage.getUsers().get(id);
        User otherUser = userStorage.getUsers().get(otherId);
        List<User> userFriends = new ArrayList<>();

        if (!user.getFriends().isEmpty() && !otherUser.getFriends().isEmpty()) {
            for (Long idFriend : otherUser.getFriends()) {
                if (user.getFriends().contains(idFriend)) {
                    userFriends.add(userStorage.getUsers().get(idFriend));
                }
            }
        }
        log.info("У пользователей с id " + id + " и id " + otherId + " {} общих друзей", userFriends.size());
        return userFriends;
    }
}
