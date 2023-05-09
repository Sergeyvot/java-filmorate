package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaService {

    Collection<Mpa> findAllMpa();

    Mpa findMpaById(int id);
}
