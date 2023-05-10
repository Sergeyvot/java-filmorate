package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Like;

/**
 * Интерфейс DAO класса LikeDaoImpl
 */
public interface LikeDao {

    Like addLike(long id, long userId);

    void removeLike(long id, long userId);
}
