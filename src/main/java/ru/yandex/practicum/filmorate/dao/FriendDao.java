package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * Интерфейс DAO класса FriendDaoImpl
 */
public interface FriendDao {

    User addFriend(int id, int friendId);

    User removeFriend(int id, int friendId);

    List<User> findAllFriends(int id);

    List<User> findMutualFriends(int id, int otherId);
}
