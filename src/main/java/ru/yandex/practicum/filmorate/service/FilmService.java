package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис класс, реализующий логику добавления, удаления лайков, получения списка популярных фильмов
 */
@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    /**
     * Внедрены зависимости интерфейсов хранилищ FilmStorage и UserStorage
     * @param filmStorage экземпляр класса InMemoryFilmStorage
     * @param userStorage экземпляр класса InMemoryUserStorage
     */
    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {

        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    /**
     * Метод для выставления лайка фильму с идентификатором id пользователем userId
     * @param id id фильма
     * @param userId id пользователя
     * @return фильм, которому выставлен лайк
     */
    public Film addLike(long id, long userId) {
        if (!filmStorage.getFilms().containsKey(id)) {
            log.error("Передан некорректный id фильма: {}", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не существует.");
        }
        if (!userStorage.getUsers().containsKey(userId)) {
            log.error("Передан некорректный id пользователя: {}", userId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не существует.");
        }
        Film film = filmStorage.getFilms().get(id);
        film.getLikes().add(userId);
        log.info("Добавлен лайк от пользователя с id {}", userId);

        return film;
    }

    /**
     * Метод для удаления лайка фильму с идентификатором id пользователем userId
     * @param id id фильма
     * @param userId id пользователя
     * @return фильм, у которого удален лайк
     */
    public Film removeLike(long id, long userId) {
        if (!filmStorage.getFilms().containsKey(id)) {
            log.error("Передана некорректный id фильма: {}", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не существует.");
        }
        if (!userStorage.getUsers().containsKey(userId)) {
            log.error("Передана некорректный id пользователя: {}", userId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не существует.");
        }
        Film film = filmStorage.getFilms().get(id);
        if (!film.getLikes().contains(userId)) {
            log.error("Пользователь с id {} не ставил лайк данному фильму", userId);
            throw new ValidationException("Пользователь с id " + userId + " не ставил лайк данному фильму.");
        }
        film.getLikes().remove(userId);
        log.info("Удален лайк от пользователя с id {}", userId);

        return film;
    }

    /**
     * Метод для получения списка популярных по количеству лайков фильмов
     * @param count щграничение списка
     * @return список фильмов
     */
    public List<Film> findPopularFilms(Integer count) {
        if (count <= 0) {
            log.error("Передан некорректный лимит списка: {}", count);
            throw new ValidationException("Лимит списка не может быть отрицательным или нулевым.");
        }
        log.info("Список из {} популярных фильмов по количеству лайков.", count);
        return filmStorage.getFilms().values().stream()
                .sorted(Comparator.comparing(f -> -1 * f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
