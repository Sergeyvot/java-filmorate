package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.impl.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.DbFilmService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки работы методов DAO и сервисного слоя
 */
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final FriendDaoImpl friendDao;
    private final MpaDaoImpl mpaDao;
    private final GenreDaoImpl genreDao;
    private final DbFilmService dbFilmService;

    @Test
    public void testFindUserById() {
        User user1 = new User("емайл@mail.ru", "логин", LocalDate.of(2000, 11, 11));
        User saveUser = userStorage.createUser(user1);
        Optional<User> userOptional = Optional.ofNullable(userStorage.findUserById(saveUser.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", saveUser.getId())
                );
    }

    @Test
    public void testRemoveUser() {
        User user = new User("емайл2@mail.ru", "login", LocalDate.of(2001, 11, 11));
        User saveUser = userStorage.createUser(user);
        userStorage.removeUser(saveUser.getId());
        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            userStorage.findUserById(saveUser.getId());
        });
        assertNotNull(thrown.getMessage());

        assertEquals(thrown.getMessage(), "Пользователь с id " + saveUser.getId() + " не найден",
                "Сообщения об ошибке не совпадают");
    }

    @Test
    public void testUpdateUser() {
        User user = new User("емайл2@mail.ru", "LoginOne", LocalDate.of(2001, 11, 11));
        User saveUser = userStorage.createUser(user);
        User updateUser = new User("email@mail.ru", "updateLogin",
                LocalDate.of(2000, 11, 11));
        updateUser.setId(saveUser.getId());
        User saveUpdateUser = userStorage.updateUser(updateUser);

        assertEquals("email@mail.ru", saveUpdateUser.getEmail(),
                "Поля обновленного пользователя не совпадают");
        assertNotEquals("емайл2@mail.ru", saveUpdateUser.getEmail(),
                "Поля пользователя не обновлены");
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User("емайл@mail.ru", "LoginAll", LocalDate.of(2001, 11, 11));
        userStorage.createUser(user1);
        User user2 = new User("email@mail.ru", "Login2",
                LocalDate.of(2000, 12, 11));
        User saveUser = userStorage.createUser(user2);
        List<User> saveUsers = userStorage.getAllUsers().stream().collect(Collectors.toList());
        assertNotEquals(0, saveUsers.size(), "Размер списка не совпадает");
        assertEquals(saveUser, saveUsers.get(saveUsers.size() - 1), "Размер списка не совпадает");
    }

    @Test
    public void testAddFriend() {
        User user1 = new User("емайл@mail.ru", "LoginAll", LocalDate.of(2001, 11, 11));
        User saveUser1 = userStorage.createUser(user1);
        User user2 = new User("email@mail.ru", "Login2",
                LocalDate.of(2000, 12, 11));
        User saveUser2 = userStorage.createUser(user2);
        User saveUserFriend = friendDao.addFriend(saveUser1.getId(), saveUser2.getId());

        assertEquals(1, saveUserFriend.getFriends().size(), "Размер списка не совпадает");
        assertTrue(saveUserFriend.getFriends().contains(saveUser2.getId()));
    }

    @Test
    public void testRemoveFriend() {
        User user1 = new User("емайл@mail.ru", "LoginAll", LocalDate.of(2001, 11, 11));
        User saveUser1 = userStorage.createUser(user1);
        User user2 = new User("email@mail.ru", "Login2",
                LocalDate.of(2000, 12, 11));
        User saveUser2 = userStorage.createUser(user2);
        User addFriend = friendDao.addFriend(saveUser1.getId(), saveUser2.getId());

        assertEquals(1, addFriend.getFriends().size(), "Размер списка не совпадает");
        addFriend = friendDao.removeFriend(saveUser1.getId(), saveUser2.getId());
        assertEquals(0, addFriend.getFriends().size(), "Размер списка не совпадает");
    }

    @Test
    public void testFindAllFriends() {
        User user1 = new User("емайл@mail.ru", "LoginAll", LocalDate.of(2001, 11, 11));
        User saveUser1 = userStorage.createUser(user1);
        User user2 = new User("email@mail.ru", "Login2",
                LocalDate.of(2000, 12, 11));
        User saveUser2 = userStorage.createUser(user2);
        User user3 = new User("mail@@yandex.ru", "LoginUser", LocalDate.of(2010, 12, 11));
        User saveUser3 = userStorage.createUser(user3);

        friendDao.addFriend(saveUser1.getId(), saveUser2.getId());
        friendDao.addFriend(saveUser1.getId(), saveUser3.getId());
        List<User> allFriends = friendDao.findAllFriends(saveUser1.getId());
        assertEquals(2, allFriends.size(), "Размер списка не совпадает");
        assertTrue(allFriends.contains(saveUser2));
        assertTrue(allFriends.contains(saveUser3));

        List<User> emptyFriends = friendDao.findAllFriends(saveUser2.getId());
        assertEquals(0, emptyFriends.size(), "Размер списка не совпадает");
    }

    @Test
    public void testFindMutualFriends() {
        User user1 = new User("емайл@mail.ru", "LoginAll", LocalDate.of(2001, 11, 11));
        User saveUser1 = userStorage.createUser(user1);
        User user2 = new User("email@mail.ru", "Login2",
                LocalDate.of(2000, 12, 11));
        User saveUser2 = userStorage.createUser(user2);
        User user3 = new User("mail@@yandex.ru", "LoginUser", LocalDate.of(2010, 12, 11));
        User saveUser3 = userStorage.createUser(user3);

        friendDao.addFriend(saveUser1.getId(), saveUser2.getId());
        friendDao.addFriend(saveUser3.getId(), saveUser2.getId());

        List<User> mutualFriends = friendDao.findMutualFriends(saveUser1.getId(), saveUser3.getId());

        assertEquals(1, mutualFriends.size(), "Размер списка не совпадает");
        assertTrue(mutualFriends.contains(saveUser2));

        List<User> emptyFriends = friendDao.findMutualFriends(saveUser2.getId(), saveUser1.getId());
        assertEquals(0, emptyFriends.size(), "Размер списка не совпадает");
    }

    @Test
    public void testFindFilmById() {
        Film film = new Film("nameFilm", "descriptionFilm",
                LocalDate.of(2000, 12, 11), 120);
        film.setMpa(new Mpa(2, "PG"));
        Film saveFilm = dbFilmService.addFilm(film);
        Film checkFilm = dbFilmService.findFilmById(saveFilm.getId());
        System.out.println(checkFilm);
        assertEquals("nameFilm", checkFilm.getName(), "Поля фильма не совпадают");
        assertEquals(2, checkFilm.getMpa().getId(), "Поля фильма не совпадают");
    }

    @Test
    public void testRemoveFilm() {
        Film film = new Film("nameFilm", "descriptionFilm",
                LocalDate.of(2000, 12, 11), 120);
        film.setMpa(new Mpa(2, "PG"));
        Film saveFilm = filmDbStorage.addFilm(film);
        filmDbStorage.removeFilm(saveFilm.getId());
        Throwable thrown = assertThrows(FilmNotFoundException.class, () -> {
            filmDbStorage.findFilmById(saveFilm.getId());
        });
        assertNotNull(thrown.getMessage());

        assertEquals(thrown.getMessage(), "Фильм с id " + saveFilm.getId() + " не найден",
                "Сообщения об ошибке не совпадают");
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film("nameFilm", "descriptionFilm",
                LocalDate.of(2000, 12, 11), 120);
        film.setMpa(new Mpa(1, "G"));
        Film saveFilm = dbFilmService.addFilm(film);
        Film updateFilm = new Film("nameFilm", "updateDescription",
                LocalDate.of(2000, 10, 12), 140);
        updateFilm.setId(saveFilm.getId());
        updateFilm.setMpa(new Mpa(3, "PG-13"));
        updateFilm.getGenres().add(new Genre(2, "Драма"));
        updateFilm.getGenres().add(new Genre(4, "Триллер"));
        updateFilm.getGenres().add(new Genre(2, "Драма"));

        Film checkFilm = dbFilmService.updateFilm(updateFilm);
        System.out.println(saveFilm);
        System.out.println(checkFilm);
        assertEquals("updateDescription", checkFilm.getDescription(), "Поля фильма не совпадают");
        assertEquals(3, checkFilm.getMpa().getId(), "Поля фильма не совпадают");
    }

    @Test
    public void testGetAllFilms() {
        Film film1 = new Film("nameFilm", "descriptionFilm",
                LocalDate.of(2000, 12, 11), 120);
        film1.setMpa(new Mpa(2, "PG"));
        Film saveFilm1 = filmDbStorage.addFilm(film1);
        Film film2 = new Film("nameFilm2", "updateDescription",
                LocalDate.of(2000, 10, 12), 140);
        film2.setMpa(new Mpa(3, "PG-13"));
        Film saveFilm2 = filmDbStorage.addFilm(film2);

        List<Film> saveFilms = (List<Film>) filmDbStorage.getAllFilms();
        assertNotEquals(0, saveFilms.size(), "Размер списка не совпадает");
        assertEquals(saveFilm2, saveFilms.get(saveFilms.size() - 1), "Содержимое списка не совпадает");
    }

    @Test
    public void testFindAllMpa() {
        List<Mpa> allMpa = (List<Mpa>) mpaDao.findAllMpa();

        assertEquals(5, allMpa.size(), "Размер списка не совпадает");
    }

    @Test
    public void testFindMpaById() {
        Mpa checkMpa = mpaDao.findMpaById(3);
        assertEquals("PG-13", checkMpa.getName(), "Поля объекта не совпадают");

        Throwable thrown = assertThrows(MpaNotFoundException.class, () -> {
            mpaDao.findMpaById(999);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(thrown.getMessage(), "Рейтинг с id 999 не найден",
                "Сообщения об ошибке не совпадают");
    }

    @Test
    public void testFindAllGenre() {
        List<Genre> allGenre = (List<Genre>) genreDao.findAllGenre();

        assertEquals(6, allGenre.size(), "Размер списка не совпадает");
    }

    @Test
    public void testFindGenreById() {
        Genre checkGenre = genreDao.findGenreById(4);
        assertEquals("Триллер", checkGenre.getName(), "Поля объекта не совпадают");

        Throwable thrown = assertThrows(GenreNotFoundException.class, () -> {
            genreDao.findGenreById(999);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(thrown.getMessage(), "Жанр с id 999 не найден",
                "Сообщения об ошибке не совпадают");
    }

    @Test
    public void testAddLike() {
        Film film1 = new Film("nameFilm", "descriptionFilm",
                LocalDate.of(2000, 12, 11), 120);
        film1.setMpa(new Mpa(1, "G"));
        Film saveFilm1 = filmDbStorage.addFilm(film1);
        User user1 = new User("емайл@mail.ru", "LoginAll", LocalDate.of(2001, 11, 11));
        User saveUser1 = userStorage.createUser(user1);

        Film checkFilm = dbFilmService.addLike(saveFilm1.getId(), saveUser1.getId());
        assertEquals(1, checkFilm.getLikes().size(), "Размер списка не совпадает");
        assertTrue(checkFilm.getLikes().contains(saveUser1.getId()), "Элементы списка не сопадают");

        Throwable thrown = assertThrows(FilmNotFoundException.class, () -> {
            dbFilmService.addLike(999, saveUser1.getId());
        });
        assertNotNull(thrown.getMessage());

        assertEquals(thrown.getMessage(), "Фильм с id 999 не существует.",
                "Сообщения об ошибке не совпадают");

        Throwable thrown2 = assertThrows(UserNotFoundException.class, () -> {
            dbFilmService.addLike(saveFilm1.getId(), 999);
        });
        assertNotNull(thrown2.getMessage());

        assertEquals(thrown2.getMessage(), "Пользователь с id 999 не существует.",
                "Сообщения об ошибке не совпадают");
    }

    @Test
    public void testRemoveLike() {
        Film film1 = new Film("nameFilm", "descriptionFilm",
                LocalDate.of(2000, 12, 11), 120);
        film1.setMpa(new Mpa(1, "G"));
        Film saveFilm1 = filmDbStorage.addFilm(film1);
        User user1 = new User("емайл@mail.ru", "LoginAll", LocalDate.of(2001, 11, 11));
        User saveUser1 = userStorage.createUser(user1);

        Film checkFilm = dbFilmService.addLike(saveFilm1.getId(), saveUser1.getId());
        checkFilm = dbFilmService.removeLike(saveFilm1.getId(), saveUser1.getId());
        assertEquals(0, checkFilm.getLikes().size(), "Размер списка не совпадает");
        assertFalse(checkFilm.getLikes().contains(saveUser1.getId()), "Элементы списка не сопадают");

        Throwable thrown = assertThrows(FilmNotFoundException.class, () -> {
            dbFilmService.removeLike(999, saveUser1.getId());
        });
        assertNotNull(thrown.getMessage());

        assertEquals(thrown.getMessage(), "Фильм с id 999 не существует.",
                "Сообщения об ошибке не совпадают");

        Throwable thrown2 = assertThrows(UserNotFoundException.class, () -> {
            dbFilmService.removeLike(saveFilm1.getId(), 999);
        });
        assertNotNull(thrown2.getMessage());

        assertEquals(thrown2.getMessage(), "Пользователь с id 999 не существует.",
                "Сообщения об ошибке не совпадают");
    }

    @Test
    public void testFindPopularFilms() {
        Film film1 = new Film("nameFilm", "descriptionFilm",
                LocalDate.of(2000, 12, 11), 120);
        film1.setMpa(new Mpa(1, "G"));
        Film saveFilm1 = filmDbStorage.addFilm(film1);
        Film film2 = new Film("nameFilm2", "updateDescription",
                LocalDate.of(2000, 10, 12), 140);
        film2.setMpa(new Mpa(2, "PG"));
        Film saveFilm2 = filmDbStorage.addFilm(film2);
        User user1 = new User("емайл@mail.ru", "LoginAll", LocalDate.of(2001, 11, 11));
        User saveUser1 = userStorage.createUser(user1);
        User user2 = new User("email@mail.ru", "Login2",
                LocalDate.of(2000, 12, 11));
        User saveUser2 = userStorage.createUser(user2);

        dbFilmService.addLike(saveFilm2.getId(), saveUser1.getId());
        dbFilmService.addLike(saveFilm2.getId(), saveUser2.getId());
        dbFilmService.addLike(saveFilm1.getId(), saveUser2.getId());

        List<Film> checkFilms = dbFilmService.findPopularFilms(2);

        assertEquals(2, checkFilms.size(), "Размер списка не совпадает");
        assertEquals(saveFilm2, checkFilms.get(0), "Элементы списка не сопадают");
    }

    @Test
    void contextLoads() {
    }
}
