package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

/**
 * Сервисный класс для работы с рейтингами фильмов
 */
@Service
public class DbMpaService implements MpaService {

    private final MpaDaoImpl mpaDao;

    public DbMpaService(MpaDaoImpl mpaDao) {
        this.mpaDao = mpaDao;
    }

    /**
     * Метод получения всех рейтингов, хранящихся в БД
     * @return - коллекция рейтингов
     */
    @Override
    public Collection<Mpa> findAllMpa() {
        return mpaDao.findAllMpa();
    }

    /**
     * Метод получения конкретного рейтинга по id
     * @param id - id рейтинга
     * @return - полученный рейтинг
     */
    @Override
    public Mpa findMpaById(int id) {
        return mpaDao.findMpaById(id);
    }
}
