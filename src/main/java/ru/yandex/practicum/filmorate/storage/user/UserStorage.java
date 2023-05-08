package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User createUser(User user);

    void removeUser(int id);

    User updateUser(User updateUser);

    Collection<User> getAllUsers();

    User findUserById(int id);
}
