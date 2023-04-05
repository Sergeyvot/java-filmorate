package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private long id;
    /**
     * Добавлено поле likes, хранящее id пользователей, поставивших фильму лайк
     */
    private Set<Long> likes = new HashSet<>();
    @NonNull
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final long duration;
}
