package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс сервисного слоя для работы с пользователями
 */
public interface UserService {

    Collection<User> findAllUsers();

    User findUserById(long id);

    User createUser(User user);

    User updateUser(User user);

    User addFriend(long id, long friendId);

    User removeFriend(long id, long friendId);

    List<User> findAllFriends(long id);

    List<User> findMutualFriends(long id, long otherId);
}
