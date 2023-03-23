package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс-контроллер для обработки эндпоинтов класса User
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    protected final Map<Integer, User> users = new HashMap<>();
    private static int id = 0;

    /**
     * Запрос на получение всех пользователей
     * @return Коллекция экземпляров класса User
     */
    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    /**
     * Запрос на сохранение нового пользователя. В методе прописана логика валидации некорректных условий
     * @param user Объект из тела запроса на сохранение
     * @return Сохраненный пользователь
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        if (user == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationException("Тело запроса не может быть пустым.");
        }
        for (User someUser : users.values()) {
            if (someUser.getLogin().equals(user.getLogin())) {
                log.error("Логин {} уже существует.", user.getLogin());
                throw new ValidationException("Пользователь с логином " + user.getLogin() + " уже зарегистрирован.");
            }
        }
        if (StringUtils.containsNone(user.getEmail(), "@")) {
            log.error("Передан некорректный адрес электронной почты: {}", user.getEmail());
            throw new ValidationException("Адрес электронной почты не может быть пустым и должен содержать " +
                    "символ @.");
        }
        //Не смог подобрать метод, объединяющий проверку на пустую строку и наличие пробелов, взаимоисключают результат
        if (StringUtils.isBlank(user.getLogin()) || StringUtils.containsWhitespace(user.getLogin())) {
            log.error("Передан некорректный логин: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Передана некорректная дата рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Зарегистрирован пользователь: {}", user.toString());

        return user;
    }

    /**
     * Запрос на обновление пользователя, имеющегося в коллекции. Также прописана логика валидации
     * @param user Объект из тела запроса на обновление
     * @return Обновленный пользователь
     */
    @PutMapping
    public User updateUser(@RequestBody User user) {

        if (user == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationException("Тело запроса не может быть пустым.");
        }
        if (!users.containsKey(user.getId())) {
            log.error("Передана некорректный id пользователя: {}", user.getId());
            throw new ValidationException("Пользователь с id " + user.getId() + " не существует.");
        }
        if (StringUtils.containsNone(user.getEmail(), "@")) {
            log.error("Передан некорректный адрес электронной почты: {}", user.getEmail());
            throw new ValidationException("Адрес электронной почты не может быть пустым и должен содержать " +
                    "символ @.");
        }
        if (StringUtils.isBlank(user.getLogin()) || StringUtils.containsWhitespace(user.getLogin())) {
            log.error("Передан некорректный логин: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Передана некорректная дата рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        log.info("Обновлен пользователь: {}", user.toString());
        users.put(user.getId(), user);
        return user;
    }
}

