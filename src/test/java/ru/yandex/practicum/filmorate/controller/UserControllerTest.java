package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест класс для проверки обработки эндпоинтов класса UserController
 */
class UserControllerTest {

    UserController controller = new UserController(new UserService(new InMemoryUserStorage()));

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

        assertTrue(controller.getUserService().getUserStorage().getUsers().isEmpty(), "Пользователь сохранен");
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

        assertTrue(controller.getUserService().getUserStorage().getUsers().isEmpty(), "Пользователь сохранен");
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

        assertTrue(controller.getUserService().getUserStorage().getUsers().isEmpty(), "Пользователь сохранен");
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

        assertTrue(controller.getUserService().getUserStorage().getUsers().isEmpty(), "Пользователь сохранен");
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

        assertTrue(controller.getUserService().getUserStorage().getUsers().isEmpty(), "Пользователь сохранен");
    }

    /**
     * Проверка сохранения поля name, если оно изначально не задано
     */
    @Test
    void shouldBeNameEqualLoginWhenNameEmpty() {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User newUser = new User("name@mail.ru", "LoginCorrect", birthday);

        controller.createUser(newUser);

        List<Long> keys = new ArrayList<>(controller.getUserService().getUserStorage().getUsers().keySet());
        User userSave = controller.getUserService().getUserStorage().getUsers().get(keys.get(0));

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

        assertTrue(controller.getUserService().getUserStorage().getUsers().isEmpty(), "Пользователь сохранен");
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

        assertEquals(controller.getUserService().getUserStorage().getUsers().size(), 1, "Пользователь 2 сохранен");
    }

    /**
     * Проверка обработки обновления пользователя при некорректном id
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenIdIncorrect() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user = new User("name@mail.ru", "LoginCorrect", birthday);
        user.setId(9999);

        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            controller.updateUser(user);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.getUserService().getUserStorage().getUsers().isEmpty(), "Пользователь обновлен");
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

        assertTrue(controller.getUserService().getUserStorage().getUsers().isEmpty(), "Пользователь обновлен");
    }

    /**
     * Проверка получения списка всех пользователей при запросе GET
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeSavedListWhenRequestGet() throws ValidationException {
        controller.getUserService().getUserStorage().getUsers().clear();
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

        assertEquals(controller.getUserService().getUserStorage().getUsers().get(user1.getId()).getLogin(), "LoginCorrect",
                "Пользователь обновлен");
        assertEquals(controller.getUserService().getUserStorage().getUsers().get(user1.getId()).getBirthday(), birthday, "Пользователь обновлен");
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

        assertEquals(controller.getUserService().getUserStorage().getUsers().get(user1.getId()).getLogin(), "LoginCorrect",
                "Пользователь обновлен");
        assertEquals(controller.getUserService().getUserStorage().getUsers().get(user1.getId()).getBirthday(), birthday, "Пользователь обновлен");
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

        assertEquals(controller.getUserService().getUserStorage().getUsers().get(user1.getId()).getLogin(), "LoginCorrect",
                "Пользователь обновлен");
        assertEquals(controller.getUserService().getUserStorage().getUsers().get(user1.getId()).getBirthday(), birthday, "Пользователь обновлен");
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

        assertEquals(controller.getUserService().getUserStorage().getUsers().get(user1.getId()).getLogin(), "LoginCorrect",
                "Пользователь обновлен");
        assertEquals(controller.getUserService().getUserStorage().getUsers().get(user1.getId()).getBirthday(), birthday, "Пользователь обновлен");
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

        assertEquals(controller.getUserService().getUserStorage().getUsers().get(user1.getId()).getLogin(), "LoginCorrect",
                "Пользователь обновлен");
        assertEquals(controller.getUserService().getUserStorage().getUsers().get(user1.getId()).getBirthday(), birthday, "Пользователь обновлен");
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

        assertEquals(controller.getUserService().getUserStorage().getUsers().get(user1.getId()).getLogin(), "LoginUpdate",
                "Пользователь обновлен");
        assertEquals(controller.getUserService().getUserStorage().getUsers().get(user1.getId()).getBirthday(), birthday2,
                "Пользователь обновлен");
    }

    /**
     * Проверка обработки добавления друга, когда все данные корректны
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeAddFriendsWhenAllIdCorrect() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User user2 = new User("name@yandex.ru", "LoginUser2", birthday2);
        controller.createUser(user2);

        controller.addFriend(user1.getId(), user2.getId());

        assertEquals(user1.getFriends().size(), 1, "Размер списка не совпадает");
        assertEquals(user2.getFriends().size(), 1, "Размер списка не совпадает");
        assertTrue(user1.getFriends().contains(user2.getId()), "Id друга не совпадает");
        assertTrue(user2.getFriends().contains(user1.getId()), "Id друга не совпадает");
    }

    /**
     * Проверка обработки добавления друга, когда id пользователя некорректен
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeNotAddFriendsWhenIdUserIncorrect() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User user2 = new User("name@yandex.ru", "LoginUser2", birthday2);
        controller.createUser(user2);

        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            controller.addFriend(9999L, user2.getId());
        });
        assertNotNull(thrown.getMessage());

        assertEquals(user1.getFriends().size(), 0, "Список создан");
        assertEquals(user2.getFriends().size(), 0, "Список создан");
        assertFalse(user1.getFriends().contains(user2.getId()), "Друг добавлен");
        assertFalse(user2.getFriends().contains(user1.getId()), "Друг добавлен");
    }

    /**
     * Проверка обработки добавления друга, когда id друга некорректен
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeNotAddFriendsWhenIdFriendIncorrect() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User user2 = new User("name@yandex.ru", "LoginUser2", birthday2);
        controller.createUser(user2);

        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            controller.addFriend(user1.getId(), 9999L);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(user1.getFriends().size(), 0, "Список создан");
        assertEquals(user2.getFriends().size(), 0, "Список создан");
        assertFalse(user1.getFriends().contains(user2.getId()), "Друг добавлен");
        assertFalse(user2.getFriends().contains(user1.getId()), "Друг добавлен");
    }

    /**
     * Проверка обработки удаления друга, когда все данные корректны
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeRemoveFriendsWhenAllIdCorrect() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User user2 = new User("name@yandex.ru", "LoginUser2", birthday2);
        controller.createUser(user2);

        controller.addFriend(user1.getId(), user2.getId());
        controller.removeFriend(user1.getId(), user2.getId());

        assertEquals(user1.getFriends().size(), 0, "Список заполнен");
        assertEquals(user2.getFriends().size(), 0, "Список заполнен");
        assertFalse(user1.getFriends().contains(user2.getId()), "Друг сохранен");
        assertFalse(user2.getFriends().contains(user1.getId()), "Друг сохранен");
    }

    /**
     * Проверка обработки удаления друга, когда id пользователя некорректен
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeNotRemoveFriendsWhenIdUserIncorrect() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User user2 = new User("name@yandex.ru", "LoginUser2", birthday2);
        controller.createUser(user2);

        controller.addFriend(user1.getId(), user2.getId());

        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            controller.removeFriend(9999L, user2.getId());
        });
        assertNotNull(thrown.getMessage());

        assertEquals(user1.getFriends().size(), 1, "Список пустой");
        assertEquals(user2.getFriends().size(), 1, "Список пустой");
        assertTrue(user1.getFriends().contains(user2.getId()), "Друг удален");
        assertTrue(user2.getFriends().contains(user1.getId()), "Друг удален");
    }

    /**
     * Проверка обработки удаления друга, когда id друга некорректен
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeNotRemoveFriendsWhenIdFriendIncorrect() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User user2 = new User("name@yandex.ru", "LoginUser2", birthday2);
        controller.createUser(user2);

        controller.addFriend(user1.getId(), user2.getId());

        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            controller.removeFriend(user1.getId(), 9999L);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(user1.getFriends().size(), 1, "Список пустой");
        assertEquals(user2.getFriends().size(), 1, "Список пустой");
        assertTrue(user1.getFriends().contains(user2.getId()), "Друг удален");
        assertTrue(user2.getFriends().contains(user1.getId()), "Друг удален");
    }

    /**
     * Проверка обработки получения списка друзей, когда все данные корректны
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeGetListFriendsWhenAllIdCorrect() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User user2 = new User("name@yandex.ru", "LoginUser2", birthday2);
        controller.createUser(user2);
        LocalDate birthday3 = LocalDate.of(2003, 10, 10);
        User user3 = new User("name@list.ru", "LoginUser3", birthday3);
        controller.createUser(user3);

        controller.addFriend(user1.getId(), user2.getId());
        controller.addFriend(user1.getId(), user3.getId());

        List<User> userFriends = controller.findAllFriends(user1.getId());
        List<User> userFriends2 = controller.findAllFriends(user2.getId());

        assertEquals(userFriends.size(), 2, "Размер списка не совпадает");
        assertEquals(userFriends2.size(), 1, "Размер списка не совпадает");
        assertEquals(userFriends.get(1), user3,"Id друга не совпадает");
        assertEquals(userFriends2.get(0), user1,"Id друга не совпадает");
    }

    /**
     * Проверка обработки получения списка друзей, когда id пользователя некорректен
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeNotGetListFriendsWhenIdUserIncorrect() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User user2 = new User("name@yandex.ru", "LoginUser2", birthday2);
        controller.createUser(user2);

        controller.getUserService().addFriend(user1.getId(), user2.getId());
        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            controller.findAllFriends(9999L);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(thrown.getMessage(), "Пользователь с id 9999 не существует.",
                "Сообщения об ошибке не совпадают");
    }

    /**
     * Проверка обработки получения списка друзей, у пользователя нет друзей
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeGetEmptyListFriendsWhenNotFriends() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User user2 = new User("name@yandex.ru", "LoginUser2", birthday2);
        controller.createUser(user2);

        List<User> userFriends = controller.findAllFriends(user1.getId());
        List<User> userFriends2 = controller.findAllFriends(user2.getId());

        assertEquals(userFriends.size(), 0, "Размер списка не совпадает");
        assertEquals(userFriends2.size(), 0, "Размер списка не совпадает");
    }

    /**
     * Проверка обработки получения списка общих друзей, когда все данные корректны
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeGetListMutualFriendsWhenAllDataCorrect() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User user2 = new User("name@yandex.ru", "LoginUser2", birthday2);
        controller.createUser(user2);
        LocalDate birthday3 = LocalDate.of(2003, 10, 10);
        User user3 = new User("name@list.ru", "LoginUser3", birthday3);
        controller.createUser(user3);

        controller.addFriend(user1.getId(), user2.getId());
        controller.addFriend(user1.getId(), user3.getId());
        List<User> userMutualFriends = controller.findMutualFriends(user2.getId(), user3.getId());

        assertEquals(userMutualFriends.size(), 1, "Размер списка не совпадает");
        assertEquals(userMutualFriends.get(0), user1,"Пользователь не совпадает");
    }

    /**
     * Проверка обработки получения списка общих друзей, когда id первого пользователя некорректен
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeGetNotListMutualFriendsWhenIdUserIncorrect() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User user2 = new User("name@yandex.ru", "LoginUser2", birthday2);
        controller.createUser(user2);
        LocalDate birthday3 = LocalDate.of(2003, 10, 10);
        User user3 = new User("name@list.ru", "LoginUser3", birthday3);
        controller.createUser(user3);

        controller.addFriend(user1.getId(), user2.getId());
        controller.addFriend(user1.getId(), user3.getId());

        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            controller.findMutualFriends(9999L, user3.getId());
        });
        assertNotNull(thrown.getMessage());

        assertEquals(thrown.getMessage(), "Пользователь с id 9999 не существует.",
                "Сообщения об ошибке не совпадают");
    }

    /**
     * Проверка обработки получения списка общих друзей, когда id второго пользователя некорректен
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeGetNotListMutualFriendsWhenIdOtherUserIncorrect() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User user2 = new User("name@yandex.ru", "LoginUser2", birthday2);
        controller.createUser(user2);
        LocalDate birthday3 = LocalDate.of(2003, 10, 10);
        User user3 = new User("name@list.ru", "LoginUser3", birthday3);
        controller.createUser(user3);

        controller.addFriend(user1.getId(), user2.getId());
        controller.addFriend(user1.getId(), user3.getId());

        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            controller.findMutualFriends(user2.getId(), 9999L);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(thrown.getMessage(), "Пользователь с id 9999 не существует.",
                "Сообщения об ошибке не совпадают");
    }

    /**
     * Проверка обработки получения списка общих друзей, когда нет общих друзей
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeGetEmptyListMutualFriendsWhenNotMutualFriends() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);
        LocalDate birthday2 = LocalDate.of(2000, 12, 12);
        User user2 = new User("name@yandex.ru", "LoginUser2", birthday2);
        controller.createUser(user2);
        LocalDate birthday3 = LocalDate.of(2003, 10, 10);
        User user3 = new User("name@list.ru", "LoginUser3", birthday3);
        controller.createUser(user3);

        controller.addFriend(user1.getId(), user2.getId());
        controller.addFriend(user2.getId(), user3.getId());

        List<User> userMutualFriends = controller.findMutualFriends(user2.getId(), user3.getId());

        assertEquals(userMutualFriends.size(), 0, "Размер списка не совпадает");
    }

    /**
     * Проверка обработки получения пользователя по Id, когда передан корректный Id
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeGetUserWhenFindByCorrectId() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);

        User saveUser = controller.findUserById(user1.getId());

        assertEquals(user1, saveUser, "Пользователи не совпадают.");
        assertNotNull(saveUser, "Пользователь не получен");
    }

    /**
     * Проверка обработки получения пользователя по Id, когда передан некорректный Id
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeNotGetUserWhenFindByInCorrectId() throws UserNotFoundException {
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginUser1", birthday);
        controller.createUser(user1);

        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            controller.findUserById(999L);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(thrown.getMessage(), "Пользователь с id 999 не найден",
                "Сообщения об ошибке не совпадают");
    }
}