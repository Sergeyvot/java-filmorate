package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс-контроллер для обработки эндпоинтов класса Film
 */
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    protected final Map<Integer, Film> films = new HashMap<>();
    private static int id = 0;

    /**
     * Запрос на получение всех фильмов
     * @return Коллекция экземпляров класса Film
     */
    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    /**
     * Запрос на сохранение нового фильма. В методе прописана логика валидации некорректных условий
     * @param film Объект из тела запроса на сохранение
     * @return Сохраненный фильм
     */
    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        if (film == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationException("Тело запроса не может быть пустым.");
        }
        for (Film someFilm : films.values()) {
            if (someFilm.getName().equals(film.getName())) {
                log.error("Фильм {} уже существует.", film.getName());
                throw new ValidationException("Фильм " + film.getName() + " уже существует.");
            }
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
        film.setId(++id);
        log.info("Добавлен фильм: {}", film.toString());
        films.put(film.getId(), film);
        return film;
    }

    /**
     * Запрос на обновление фильма, имеющегося в коллекции. Также прописана логика валидации
     * @param film Объект из тела запроса на обновление
     * @return Обновленный фильм
     */
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {

        if (film == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationException("Тело запроса не может быть пустым.");
        }
        if (!films.containsKey(film.getId())) {
            log.error("Передана некорректный id фильма: {}", film.getId());
            throw new ValidationException("Фильм с id " + film.getId() + " не существует.");
        }
        if (StringUtils.isBlank(film.getName())) {
            log.error("На обновление передано пустое название фильма.");
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
        log.info("Обновлен фильм: {}", film.toString());
        films.put(film.getId(), film);
        return film;
    }
}
