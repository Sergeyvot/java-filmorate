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
    private String name;
    @NonNull
    private final String email;
    private final String login;
    private final LocalDate birthday;
}
