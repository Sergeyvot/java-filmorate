package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    private int id;
    private Set<Integer> likes = new HashSet<>();
    /**
     * Добавлены поля жанров и рейтинга фильма
     */
    private Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
    @NonNull
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;
    private Mpa mpa;

    /**
     * Метод, используемый в DAO слое для заполнения таблицы films
     * @return - Map, ключ - название поля таблицы, значение - поле объекта Film
     */
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa_id", mpa.getId());
        return values;
    }
}
