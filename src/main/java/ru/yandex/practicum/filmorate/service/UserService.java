package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс сервисного слоя для работы с пользователями
 */
public interface UserService {

    Collection<User> findAllUsers();

    User findUserById(int id);

    User createUser(User user);

    User updateUser(User user);

    User addFriend(int id, int friendId);

    User removeFriend(int id, int friendId);

    List<User> findAllFriends(int id);

    List<User> findMutualFriends(int id, int otherId);
}
