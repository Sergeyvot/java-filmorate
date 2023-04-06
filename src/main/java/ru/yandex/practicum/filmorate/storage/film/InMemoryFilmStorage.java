package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

/**
 * Класс, имплементирующий интерфейс FilmStorage
 */
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    protected final Map<Long, Film> films = new HashMap<>();
    private static long id = 0;

    /**
     * Метод добавления фильма в хранилище
     *
     * @param film экземпляр класса Film
     * @return сохраненный фильм
     */
    @Override
    public Film addFilm(Film film) {
        for (Film someFilm : films.values()) {
            if (someFilm.getName().equals(film.getName())) {
                log.error("Фильм {} уже существует.", film.getName());
                throw new ValidationException("Фильм " + film.getName() + " уже существует.");
            }
        }
        checkValidationFilm(film);
        film.setId(++id);
        log.info("Добавлен фильм: {}", film);
        films.put(film.getId(), film);

        return film;
    }

    /**
     * Метод удаления фильма из хранилища
     *
     * @param id id фильма
     */
    @Override
    public void removeFilm(Long id) {
        if (!films.containsKey(id)) {
            log.error("Передана некорректный id фильма: {}", id);
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", id));
        }
        log.info("Удален фильм: {}", films.get(id));

        films.remove(id);
    }

    /**
     * Метод обновления фильма
     *
     * @param updateFilm обновленный фильм
     * @return сохраненный обновленный фильм
     */
    @Override
    public Film updateFilm(Film updateFilm) {
        checkValidationFilm(updateFilm);
        if (!films.containsKey(updateFilm.getId())) {
            log.error("Передана некорректный id фильма: {}", updateFilm.getId());
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", updateFilm.getId()));
        }
        log.info("Обновлен фильм: {}", updateFilm);
        films.put(updateFilm.getId(), updateFilm);

        return updateFilm;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    /**
     * Метод получения списка всех фильмов
     *
     * @return список всех фильмов
     */
    @Override
    public Collection<Film> getAllFilms() {
        log.info("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    /**
     * Метод получения конкретного фильма по id
     *
     * @param id id фильма
     * @return фильм с идентификатором id
     */
    @Override
    public Film findFilmById(Long id) {
        return films.values().stream()
                .filter(f -> f.getId() == (id))
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id %d не найден", id)));
    }

    /**
     * Проверка валидации параметров объекта Film
     * @param film объект Film
     * @throws ValidationException
     */
    private void checkValidationFilm(Film film) throws ValidationException {

        if (film == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationException("Тело запроса не может быть пустым.");
        }
        if (StringUtils.isBlank(film.getName())) {
            log.error("Передано пустое название фильма.");
            throw new ValidationException("Название фильиа не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.error("Описание фильма больше допустимого количества символов: {}", film.getDescription().length());
            throw new ValidationException("Описание фильиа должно содержать нее более 200 символов.");
        }
        if (film.getDuration() <= 0) {
            log.error("Передана некорректная продолжительность фильма: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильиа должна быть положительной.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Передана некорректная дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть ранее 28.12.1895г.");
        }
    }
}
