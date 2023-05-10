package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DbFilmService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(DbFilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAllFilms() {

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

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") long id) {

        return filmService.findFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") long id,
                        @PathVariable("userId") long userId
    ) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable("id") long id,
                           @PathVariable("userId") long userId
    ) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count
            ) {
        return filmService.findPopularFilms(count);
    }
}
