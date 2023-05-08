package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.DbUserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final DbUserService userService;

    @GetMapping
    public Collection<User> findAllUsers() {

        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable("id") Integer id) {

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
    public User addFriend(@PathVariable("id") Integer id,
                          @PathVariable("friendId") Integer friendId
    ) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable("id") Integer id,
                             @PathVariable("friendId") Integer friendId
    ) {
        return userService.removeFriend(id,friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable("id") Integer id) {

        return userService.findAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable("id") Integer id,
                                        @PathVariable("otherId") Integer otherId
    ) {
        return userService.findMutualFriends(id, otherId);
    }
}

