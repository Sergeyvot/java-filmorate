package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class User {
    private int id;
    private String name;
    private Set<Integer> friends = new HashSet<>();
    @NonNull
    private final String email;
    private final String login;
    private final LocalDate birthday;

    /**
     * Метод, используемый в DAO слое для заполнения таблицы users
     * @return - Map, ключ - название поля таблицы, значение - поле объекта User
     */
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("email", email);
        values.put("login", login);
        values.put("birthday", birthday);
        return values;
    }
}
