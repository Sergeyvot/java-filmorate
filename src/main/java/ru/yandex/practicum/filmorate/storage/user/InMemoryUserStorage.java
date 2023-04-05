package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс, имплементирующий интерфейс UserStorage
 */
@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    protected final Map<Long, User> users = new HashMap<>();
    private static long id = 0;

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    /**
     * Метод добавления нового пользователя
     * @param user экземпляр класса User
     * @return сохраненный пользователь
     */
    @Override
    public User createUser(User user) {
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
        log.info("Зарегистрирован пользователь: {}", user);

        return user;
    }

    /**
     * Метод удаления конкретного пользователя
     * @param id id пользователя
     */
    @Override
    public void removeUser(long id) {
        if (!users.containsKey(id)) {
            log.error("Передана некорректный id пользователя: {}", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не существует.");
        }
        users.remove(id);
    }

    /**
     * Метод обновления пользователя
     * @param updateUser обновленный пользователь
     * @return сохраненный обновленный пользователь
     */
    @Override
    public User updateUser(User updateUser) {
        if (updateUser == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationException("Тело запроса не может быть пустым.");
        }
        if (!users.containsKey(updateUser.getId())) {
            log.error("Передана некорректный id пользователя: {}", updateUser.getId());
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", updateUser.getId()));
        }
        if (StringUtils.containsNone(updateUser.getEmail(), "@")) {
            log.error("Передан некорректный адрес электронной почты: {}", updateUser.getEmail());
            throw new ValidationException("Адрес электронной почты не может быть пустым и должен содержать " +
                    "символ @.");
        }
        if (StringUtils.isBlank(updateUser.getLogin()) || StringUtils.containsWhitespace(updateUser.getLogin())) {
            log.error("Передан некорректный логин: {}", updateUser.getLogin());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
        if (updateUser.getBirthday().isAfter(LocalDate.now())) {
            log.error("Передана некорректная дата рождения: {}", updateUser.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (StringUtils.isBlank(updateUser.getName())) {
            updateUser.setName(updateUser.getLogin());
        }
        log.info("Обновлен пользователь: {}", updateUser);
        users.put(updateUser.getId(), updateUser);
        return updateUser;
    }

    /**
     * Метод получения списка всех пользователей
     * @return список всех пользователей
     */
    @Override
    public Collection<User> getAllUsers() {
        log.info("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    /**
     * Метод получения конкретного пользователя
     * @param id id пользователя
     * @return пользователь с идентификатором id
     */
    @Override
    public User findUserById(Long id) {

        return users.values().stream()
                .filter(u -> u.getId() == (id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", id)));
    }
}
