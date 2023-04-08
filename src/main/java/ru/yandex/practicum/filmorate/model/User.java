package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private long id;
    private String name;
    /**
     * Добавлено поле friends, хранящее id друзей пользоватееля
     */
    private Set<Long> friends = new HashSet<>();
    @NonNull
    private final String email;
    private final String login;
    private final LocalDate birthday;
}
