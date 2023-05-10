package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

/**
 * Интерфейс DAO класса MpaDaoImpl
 */
public interface MpaDao {

    Collection<Mpa> findAllMpa();

    Mpa findMpaById(int id);
}
