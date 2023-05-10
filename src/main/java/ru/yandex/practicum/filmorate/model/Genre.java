package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.Objects;

/**
 * Класс объекта Genre
 */
@Data
public class Genre {
    private final int id;
    private final String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return id == genre.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
