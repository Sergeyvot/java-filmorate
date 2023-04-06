package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {

        this.userService = userService;
    }

    public UserService getUserService() {
        return userService;
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        //Убрано обращение к полю класса сервиса, логика реализуется через метод класса. Аналогично в других методах
        return userService.findAllUsers();
    }

    /**
     * Запрос на получение конкретного пользователя по id
     *
     * @param id - переменная пути, id пользователя
     * @return пользователь с идентификатором id
     */
    @GetMapping("/{id}")
    public User findUserById(@PathVariable("id") Long id) {

        return userService.findUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {

        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {

        return userService.updateUser(user);
    }

    /**
     * Запрос на добавление в друзья
     *
     * @param id       id пользователя, которому добавляется друг
     * @param friendId id пользователя, который добавляется в друзья
     * @return пользователь, к которому добавился друг
     */
    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Long id,
                          @PathVariable("friendId") Long friendId
    ) {
        return userService.addFriend(id, friendId);
    }

    /**
     * Запрос на удаление из друзей
     *
     * @param id       id пользователя, у которого удаляется друг
     * @param friendId id пользователя, который удаляется из друзей
     * @return пользователь, у которого удаляется друг
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable("id") Long id,
                             @PathVariable("friendId") Long friendId
    ) {
        return userService.removeFriend(id, friendId);
    }

    /**
     * Запрос на получение списка друзей пользователя с идентификатором id
     *
     * @param id id пользователя
     * @return список друзей пользователя
     */
    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable("id") Long id) {

        return userService.findAllFriends(id);
    }

    /**
     * Запрос на получение списка общих друзей двух пользователей
     *
     * @param id      id первого пользователя
     * @param otherId id второго пользователя
     * @return список общих друзей
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable("id") Long id,
                                        @PathVariable("otherId") Long otherId
    ) {
        return userService.findMutualFriends(id, otherId);
    }
}

