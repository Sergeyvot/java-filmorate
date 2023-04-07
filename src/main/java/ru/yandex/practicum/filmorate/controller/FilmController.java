package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    public FilmService getFilmService() {
        return filmService;
    }

    @GetMapping
    public Collection<Film> findAllFilms() {
        //Убрано обращение к полю класса сервиса, логика реализуется через метод класса. Аналогично в других методах
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {

        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {

        return filmService.updateFilm(film);
    }

    /**
     * Запрос на получение конкретного фильма по id
     * @param id - переменная пути, id фильма
     * @return фильм с идентификатором id
     */
    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") Long id) {

        return filmService.findFilmById(id);
    }

    /**
     * Запрос на проставление пользователем с userId лайка фильму с идентификатором id
     * @param id id фильма
     * @param userId id пользователя
     * @return фильм с идентификатором id, которому выставлен лайк
     */
    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") Long id,
                        @PathVariable("userId") Long userId
    ) {
        return filmService.addLike(id, userId);
    }

    /**
     * Запрос на удаление лайка фильму с идентификатором id пользователем с userId
     * @param id id фильма
     * @param userId id пользователя
     * @return фильм с идентификатором id, у которого удален лайк
     */
    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable("id") Long id,
                           @PathVariable("userId") Long userId
    ) {
        return filmService.removeLike(id, userId);
    }

    /**
     * Запрос на получение первых {count} фильмов по количеству лайков
     * @param count ограничение списка фильмов, по умолчанию параметр равен 10
     * @return список фильмов по количеству лайков
     */
    @GetMapping("/popular")
    public List<Film> findPopularFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count
            ) {
        return filmService.findPopularFilms(count);
    }
}
