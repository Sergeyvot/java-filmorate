package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.DbUserService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(DbUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAllUsers() {

        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable("id") long id) {

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

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") long id,
                          @PathVariable("friendId") long friendId
    ) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable("id") long id,
                             @PathVariable("friendId") long friendId
    ) {
        return userService.removeFriend(id,friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable("id") long id) {

        return userService.findAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable("id") long id,
                                        @PathVariable("otherId") long otherId
    ) {
        return userService.findMutualFriends(id, otherId);
    }
}

