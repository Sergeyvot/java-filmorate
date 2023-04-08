package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

/**
 * Интерфейс для методов добавления, удаления и модификации объектов User
 */
public interface UserStorage {

    User createUser(User user);

    void removeUser(long id);

    User updateUser(User updateUser);

    Map<Long, User> getUsers();

    Collection<User> getAllUsers();

    User findUserById(Long id);
}
