package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

/**
 * Класс - модель данных приложения.
 */
@Data
public class User {
    private int id;
    @NonNull
    private final String email;
    @NonNull
    private final String login;
    private String name;
    private final LocalDate birthday;
}
