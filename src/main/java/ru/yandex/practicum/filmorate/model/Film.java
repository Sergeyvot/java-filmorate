package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

/**
 * Класс - модель данных приложения.
 */
@Data
public class Film {
    private int id;
    @NonNull
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final long duration;
}
