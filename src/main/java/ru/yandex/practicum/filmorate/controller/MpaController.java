package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.DbMpaService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

/**
 * Контроллер для обработки эндпоинтов на запросы рейтингов
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
@Slf4j
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(DbMpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public Collection<Mpa> findAllMpa() {

        return mpaService.findAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa findMpaById(@PathVariable("id") int id) {

        return mpaService.findMpaById(id);
    }
}
