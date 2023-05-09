package ru.yandex.practicum.filmorate.model;

import lombok.Data;

/**
 * Класс объекта Like
 */
@Data
public class Like {
    private final long filmId;
    private final long userId;
}
