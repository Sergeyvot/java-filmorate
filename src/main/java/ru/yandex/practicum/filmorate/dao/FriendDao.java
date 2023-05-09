package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * Интерфейс DAO класса FriendDaoImpl
 */
public interface FriendDao {

    User addFriend(long id, long friendId);

    User removeFriend(long id, long friendId);

    List<User> findAllFriends(long id);

    List<User> findMutualFriends(long id, long otherId);
}
