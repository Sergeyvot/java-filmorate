package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User createUser(User user);

    void removeUser(long id);

    User updateUser(User updateUser);

    Collection<User> getAllUsers();

    User findUserById(long id);
}
