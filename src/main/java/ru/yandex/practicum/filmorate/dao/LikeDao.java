package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Like;

/**
 * Интерфейс DAO класса LikeDaoImpl
 */
public interface LikeDao {

    Like addLike(int id, int userId);

    void removeLike(int id, int userId);
}
