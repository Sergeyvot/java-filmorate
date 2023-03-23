package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест класс для проверки обработки эндпоинтов класса UserController
 */
class UserControllerTest {

    UserController controller = new UserController();

    /**
     * Проверка обработки создания пользователя при пустом поле email
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeEmptyMupWhenEmailEmpty() throws ValidationException {
        User newUser = new User("", "LoginName", LocalDate.of(2000, 11, 11));

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.createUser(newUser);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.users.isEmpty(), "Пользователь сохранен");
    }

    /**
     * Проверка обработки создания пользователя при некорректном email
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeEmptyMupWhenEmailIncorrect() throws ValidationException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User newUser = new User("name.mail.ru", "LoginName", birthday);

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.createUser(newUser);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.users.isEmpty(), "Пользователь сохранен");
    }

    /**
     * Проверка обработки создания пользователя при пустом поле login
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeEmptyMupWhenLoginEmpty() throws ValidationException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User newUser = new User("name@mail.ru", "", birthday);

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.createUser(newUser);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.users.isEmpty(), "Пользователь сохранен");
    }

    /**
     * Проверка обработки создания пользователя при некорректном логине
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeEmptyMupWhenLoginIncorrect() throws ValidationException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User newUser = new User("name@mail.ru", "Login Incorrect", birthday);

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.createUser(newUser);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.users.isEmpty(), "Пользователь сохранен");
    }

    /**
     * Проверка обработки создания пользователя при некорректной дате рождения пользователя
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeEmptyMupWhenBirthdayIncorrect() throws ValidationException {
        LocalDate birthday = LocalDate.of(2046, 11, 11);
        User newUser = new User("name@mail.ru", "LoginIncorrect", birthday);

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.createUser(newUser);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.users.isEmpty(), "Пользователь сохранен");
    }

    /**
     * Проверка сохранения поля name, если оно изначально не задано
     */
    @Test
    void shouldBeNameEqualLoginWhenNameEmpty() {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User newUser = new User("name@mail.ru", "LoginCorrect", birthday);

        controller.createUser(newUser);

        List<Integer> keys = new ArrayList<>(controller.users.keySet());
        User userSave = controller.users.get(keys.get(0));

        assertEquals(userSave.getName(), "LoginCorrect", "Поля не совпадают.");
    }

    /**
     * Проверка обработки создания пользователя при пустом теле запроса
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeEmptyMupWhenRequestBodyEmpty() throws ValidationException {

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.createUser(null);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.users.isEmpty(), "Пользователь сохранен");
    }

    /**
     * Проверка обработки создания пользователя при совпадающем логине
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeSavedWhenLoginMatches() throws ValidationException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginIncorrect", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2001, 12, 12);
        User user2 = new User("name2@mail.ru", "LoginIncorrect", birthday2);

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.createUser(user2);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(controller.users.size(), 1, "Пользователь 2 сохранен");
    }

    /**
     * Проверка обработки обновления пользователя при некорректном id
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenIdIncorrect() throws ValidationException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user = new User("name@mail.ru", "LoginCorrect", birthday);
        user.setId(9999);

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.updateUser(user);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.users.isEmpty(), "Пользователь обновлен");
    }

    /**
     * Проверка обработки обновления пользователя при пустом теле запроса
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenRequestBodyEmpty() throws ValidationException {

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.updateUser(null);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.users.isEmpty(), "Пользователь обновлен");
    }

    /**
     * Проверка получения списка всех пользователей при запросе GET
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeSavedListWhenRequestGet() throws ValidationException {
        controller.users.clear();
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2001, 12, 12);
        User user2 = new User("name2@mail.ru", "LoginCorrectTwo", birthday2);
        controller.createUser(user2);

        List<User> usersSave = new ArrayList<>(controller.findAllUsers());

        assertEquals(usersSave.get(0).getLogin(), "LoginCorrect", "Поля не совпадают.");
        assertEquals(usersSave.get(1).getEmail(), "name2@mail.ru", "Поля не совпадают");
        assertEquals(usersSave.size(), 2, "Размер списка не совпадает.");
    }

    /**
     * Проверка обработки обновления пользователя при пустом поле email
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenEmailEmpty() throws ValidationException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.createUser(user1);
        User userUpdate = new User("", "LoginUpdate", LocalDate.of(2000, 12, 11));
        userUpdate.setId(user1.getId());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.updateUser(userUpdate);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(controller.users.get(user1.getId()).getLogin(), "LoginCorrect",
                "Пользователь обновлен");
        assertEquals(controller.users.get(user1.getId()).getBirthday(), birthday, "Пользователь обновлен");
    }

    /**
     * Проверка обработки обновления пользователя при некорректном email
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenEmailIncorrect() throws ValidationException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User userUpdate = new User("name.mail.ru", "LoginUpdate", birthday2);
        userUpdate.setId(user1.getId());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.updateUser(userUpdate);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(controller.users.get(user1.getId()).getLogin(), "LoginCorrect",
                "Пользователь обновлен");
        assertEquals(controller.users.get(user1.getId()).getBirthday(), birthday, "Пользователь обновлен");
    }

    /**
     * Проверка обработки обновления пользователя при пустом поле login
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenLoginEmpty() throws ValidationException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User userUpdate = new User("name@mail.ru", "", birthday2);
        userUpdate.setId(user1.getId());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.updateUser(userUpdate);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(controller.users.get(user1.getId()).getLogin(), "LoginCorrect",
                "Пользователь обновлен");
        assertEquals(controller.users.get(user1.getId()).getBirthday(), birthday, "Пользователь обновлен");
    }

    /**
     * Проверка обработки обновления пользователя при некорректном логине
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenLoginIncorrect() throws ValidationException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User userUpdate = new User("name@mail.ru", "Login Incorrect", birthday2);
        userUpdate.setId(user1.getId());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.updateUser(userUpdate);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(controller.users.get(user1.getId()).getLogin(), "LoginCorrect",
                "Пользователь обновлен");
        assertEquals(controller.users.get(user1.getId()).getBirthday(), birthday, "Пользователь обновлен");
    }

    /**
     * Проверка обработки обновления пользователя при некорректной дате рождения пользователя
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenBirthdayIncorrect() throws ValidationException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2046, 11, 11);
        User userUpdate = new User("name@mail.ru", "LoginCorrect", birthday2);
        userUpdate.setId(user1.getId());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.updateUser(userUpdate);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(controller.users.get(user1.getId()).getLogin(), "LoginCorrect",
                "Пользователь обновлен");
        assertEquals(controller.users.get(user1.getId()).getBirthday(), birthday, "Пользователь обновлен");
    }

    /**
     * Проверка обработки обновления пользователя при добавлении пользователя с корректными данными
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeUpdateWhenAllDatesCorrect() throws ValidationException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User userUpdate = new User("name@mail.ru", "LoginUpdate", birthday2);
        userUpdate.setId(user1.getId());

        controller.updateUser(userUpdate);

        assertEquals(controller.users.get(user1.getId()).getLogin(), "LoginUpdate",
                "Пользователь обновлен");
        assertEquals(controller.users.get(user1.getId()).getBirthday(), birthday2, "Пользователь обновлен");
    }
}