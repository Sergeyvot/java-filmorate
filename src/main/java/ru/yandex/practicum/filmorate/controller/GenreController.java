package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.DbGenreService;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

/**
 * Контроллер для обработки эндпоинтов на запросы жанров
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
@Slf4j
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(DbGenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public Collection<Genre> findAllGenre() {

        return genreService.findAllGenre();
    }

    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable("id") int id) {

        return genreService.findGenreById(id);
    }
}
