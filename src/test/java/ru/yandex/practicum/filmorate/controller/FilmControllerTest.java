package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тест класс для проверки обработки эндпоинтов класса FilmController
 */
class FilmControllerTest {

    FilmController controller = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));

    /**
     * Проверка обработки сохранения фильма при пустом теле запроса
     * @throws ValidationException
     */
    @Test
    void shouldBeEmptyMupWhenRequestBodyEmpty() throws ValidationException {

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.addFilm(null);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.getFilmService().getFilmStorage().getFilms().isEmpty(), "Фильм сохранен");
    }

    /**
     * Проверка обработки сохранения фильма при совпадении названия с имеющимся в коллекции
     * @throws ValidationException
     */
    @Test
    void shouldNotBeSavedWhenNameMatches() throws ValidationException {

        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("nameFilm1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate releaseDate2 = LocalDate.of(2001, 12, 12);
        Film film2 = new Film("nameFilm1", "descriptionFilm2", releaseDate2, 130);

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film2);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(controller.getFilmService().getFilmStorage().getFilms().size(), 1, "Фильм 2 сохранен");
    }

    /**
     * Проверка обработки сохранения фильма при добавлении фильма с пустым названием
     * @throws ValidationException
     */
    @Test
    void shouldBeEmptyMupWhenNameEmpty() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("", "descriptionFilm1", releaseDate, 120);

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film1);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.getFilmService().getFilmStorage().getFilms().isEmpty(), "Фильм сохранен");
    }

    /**
     * Проверка обработки сохранения фильма при добавлении фильма с описанием более 200 символов
     * @throws ValidationException
     */
    @Test
    void shouldBeEmptyMupWhenDescriptionMoreThan200Characters() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("nameFilm1", "Очень длинное описание фильма, включающее в себя" +
                " подробно описание каждого действия, а также характеры героев и краткое содержание следующих" +
                " серий. Также указан полный список артистов, гримеров, осветителей, ассистентов и другая" +
                " абсолютно ненужная информация.", releaseDate, 120);

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film1);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.getFilmService().getFilmStorage().getFilms().isEmpty(), "Фильм сохранен");
    }

    /**
     * Проверка обработки сохранения фильма при добавлении фильма с некорректной продолжительностью
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeEmptyMupWhenDurationIncorrect() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("nameFilm1", "descriptionFilm1", releaseDate, -120);

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film1);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.getFilmService().getFilmStorage().getFilms().isEmpty(), "Фильм сохранен");
    }

    /**
     * Проверка обработки сохранения фильма при добавлении фильма с некорректной датой релиза
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeEmptyMupWhenReleaseDateIncorrect() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(1895, 12, 27);
        Film film1 = new Film("nameFilm1", "descriptionFilm1", releaseDate, 120);

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film1);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.getFilmService().getFilmStorage().getFilms().isEmpty(), "Фильм сохранен");
    }

    /**
     * Проверка обработки обновления фильма при пустом теле запроса
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenRequestBodyEmpty() throws ValidationException {

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.updateFilm(null);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.getFilmService().getFilmStorage().getFilms().isEmpty(), "Фильм обновлен");
    }

    /**
     * Проверка обработки обновления фильма при некорректном id
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenIdIncorrect() throws FilmNotFoundException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film = new Film("nameFilm1", "descriptionFilm1", releaseDate, 120);
        film.setId(9999);

        Throwable thrown = assertThrows(FilmNotFoundException.class, () -> {
            controller.updateFilm(film);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.getFilmService().getFilmStorage().getFilms().isEmpty(), "Фильм обновлен");
    }

    /**
     * Проверка получения списка всех фильмов при запросе GET
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeSavedListWhenRequestGet() throws ValidationException {
        controller.getFilmService().getFilmStorage().getFilms().clear();
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("nameFilm1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate releaseDate2 = LocalDate.of(2001, 12, 12);
        Film film2 = new Film("nameFilm2", "descriptionFilm2", releaseDate2, 140);
        controller.addFilm(film2);

        List<Film> filmsSave = new ArrayList<>(controller.findAllFilms());

        assertFalse(filmsSave.isEmpty(), "Список пустой.");
        assertEquals(filmsSave.size(), 2, "Размер списка не совпадает.");
    }

    /**
     * Проверка обработки обновления фильма при передаче фильма с пустым названием
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenNameEmpty() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate releaseDate2 = LocalDate.of(2000, 10, 11);
        Film filmUpdate = new Film("", "descriptionFilmUpdate", releaseDate2, 130);
        filmUpdate.setId(film1.getId());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.updateFilm(filmUpdate);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(controller.getFilmService().getFilmStorage().getFilms().get(film1.getId()).getDescription(),
                "descriptionFilm1", "Фильм обновлен");
        assertEquals(controller.getFilmService().getFilmStorage().getFilms().get(film1.getId()).getDuration(),
                120, "Фильм обновлен");
    }

    /**
     * Проверка обработки обновления фильма при добавлении описания фильма более 200 символов
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenDescriptionMoreThan200Characters() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate releaseDate2 = LocalDate.of(2000, 11, 25);
        Film filmUpdate = new Film("name1", "Очень длинное описание фильма, включающее в себя" +
                " подробно описание каждого действия, а также характеры героев и краткое содержание следующих" +
                " серий. Также указан полный список артистов, гримеров, осветителей, ассистентов и другая" +
                " абсолютно ненужная информация.", releaseDate2, 140);
        filmUpdate.setId(film1.getId());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.updateFilm(filmUpdate);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(controller.getFilmService().getFilmStorage().getFilms().get(film1.getId()).getDescription(),
                "descriptionFilm1", "Фильм обновлен");
        assertEquals(controller.getFilmService().getFilmStorage().getFilms().get(film1.getId()).getDuration(),
                120, "Фильм обновлен");
    }

    /**
     * Проверка обработки обновления фильма при добавлении фильма с некорректной продолжительностью
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenDurationIncorrect() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate releaseDate2 = LocalDate.of(2000, 11, 11);
        Film filmUpdate = new Film("name1", "descriptionFilm1Update", releaseDate2, -120);
        filmUpdate.setId(film1.getId());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.updateFilm(filmUpdate);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(controller.getFilmService().getFilmStorage().getFilms().get(film1.getId()).getDescription(),
                "descriptionFilm1", "Фильм обновлен");
        assertEquals(controller.getFilmService().getFilmStorage().getFilms().get(film1.getId()).getDuration(),
                120, "Фильм обновлен");
    }

    /**
     * Проверка обработки обновления фильма при добавлении фильма с некорректной датой релиза
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenReleaseDateIncorrect() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate releaseDate2 = LocalDate.of(1895, 12, 27);
        Film filmUpdate = new Film("name1", "descriptionFilm1", releaseDate2, 120);
        filmUpdate.setId(film1.getId());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.updateFilm(filmUpdate);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(controller.getFilmService().getFilmStorage().getFilms().get(film1.getId()).getDescription(),
                "descriptionFilm1", "Фильм обновлен");
        assertEquals(controller.getFilmService().getFilmStorage().getFilms().get(film1.getId()).getDuration(),
                120, "Фильм обновлен");
    }

    /**
     * Проверка обработки обновления фильма при добавлении фильма с корректными данными
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeUpdateWhenAllDatesCorrect() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate releaseDate2 = LocalDate.of(2001, 1, 27);
        Film filmUpdate = new Film("name1", "descriptionFilmUpdate", releaseDate2, 140);
        filmUpdate.setId(film1.getId());

        controller.updateFilm(filmUpdate);

        assertEquals(controller.getFilmService().getFilmStorage().getFilms().get(film1.getId()).getDescription(),
                "descriptionFilmUpdate", "Фильм не обновлен");
        assertEquals(controller.getFilmService().getFilmStorage().getFilms().get(film1.getId()).getDuration(),
                140, "Фильм не обновлен");
    }

    /**
     * Проверка обработки добавления лайка с корректными id
     * @throws ValidationException
     */
    @Test
    void shouldBeAddLikeWhenAllIdCorrect() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.getFilmService().getUserStorage().createUser(user1);
        controller.addLike(film1.getId(), user1.getId());

        assertFalse(film1.getLikes().isEmpty(), "Список лайков пустой");
        assertTrue(film1.getLikes().contains(user1.getId()), "Id пользователя не совпадает");
    }

    /**
     * Проверка обработки добавления лайка, когда id фильма некорректный
     * @throws FilmNotFoundException
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeNotAddLikeWhenIdFilmIncorrect() throws FilmNotFoundException, UserNotFoundException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.getFilmService().getUserStorage().createUser(user1);

        Throwable thrown = assertThrows(FilmNotFoundException.class, () -> {
            controller.addLike(9999L, user1.getId());
        });
        assertNotNull(thrown.getMessage());

        assertTrue(film1.getLikes().isEmpty(), "Список лайков не пустой");
    }

    /**
     * Проверка обработки добавления лайка, когда id пользователя некорректный
     * @throws FilmNotFoundException
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeNotAddLikeWhenIdUserIncorrect() throws FilmNotFoundException, UserNotFoundException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.getFilmService().getUserStorage().createUser(user1);

        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            controller.addLike(film1.getId(), 9999L);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(film1.getLikes().isEmpty(), "Список лайков не пустой");
    }

    /**
     * Проверка обработки удаления лайка с корректными id
     * @throws ValidationException
     */
    @Test
    void shouldBeRemoveLikeWhenAllIdCorrect() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.getFilmService().getUserStorage().createUser(user1);
        controller.addLike(film1.getId(), user1.getId());

        controller.removeLike(film1.getId(), user1.getId());

        assertTrue(film1.getLikes().isEmpty(), "Список лайков не пустой");
    }

    /**
     * Проверка обработки удаления лайка, когда id фильма некорректный
     * @throws FilmNotFoundException
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeNotRemoveLikeWhenIdFilmIncorrect() throws FilmNotFoundException, UserNotFoundException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.getFilmService().getUserStorage().createUser(user1);
        controller.addLike(film1.getId(), user1.getId());

        Throwable thrown = assertThrows(FilmNotFoundException.class, () -> {
            controller.removeLike(9999L, user1.getId());
        });
        assertNotNull(thrown.getMessage());

        assertFalse(film1.getLikes().isEmpty(), "Список лайков пустой");
    }

    /**
     * Проверка обработки удаления лайка, когда id пользователя некорректный
     * @throws FilmNotFoundException
     * @throws UserNotFoundException
     */
    @Test
    void shouldBeNotRemoveLikeWhenIdUserIncorrect() throws FilmNotFoundException, UserNotFoundException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.getFilmService().getUserStorage().createUser(user1);
        controller.addLike(film1.getId(), user1.getId());

        Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
            controller.removeLike(film1.getId(), 9999L);
        });
        assertNotNull(thrown.getMessage());

        assertFalse(film1.getLikes().isEmpty(), "Список лайков пустой");
    }

    /**
     * Проверка обработки удаления лайка, когда удаляющий пользователь не ставил лайк
     * @throws ValidationException
     */
    @Test
    void shouldBeNotRemoveLikeWhenUserNotAddLike() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.getFilmService().getUserStorage().createUser(user1);
        LocalDate birthday2 = LocalDate.of(2002, 10, 10);
        User user2 = new User("name@yandex.ru", "Login2", birthday2);
        controller.getFilmService().getUserStorage().createUser(user2);

        controller.addLike(film1.getId(), user1.getId());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.removeLike(film1.getId(), user2.getId());
        });
        assertNotNull(thrown.getMessage());

        assertEquals(film1.getLikes().size(),1, "Размер списка не совпадает");
        assertTrue(film1.getLikes().contains(user1.getId()), "Id пользователя не совпадает");
        assertFalse(film1.getLikes().contains(user2.getId()), "Id пользователя не совпадает");
    }

    /**
     * Проверка обработки получения списка популярных фильмов с корректным count
     */
    @Test
    void shouldBeGetListPopularFilms() {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate releaseDate2 = LocalDate.of(2002, 10, 10);
        Film film2 = new Film("name2", "descriptionFilm2", releaseDate2, 130);
        controller.addFilm(film2);
        LocalDate releaseDate3 = LocalDate.of(2003, 12, 12);
        Film film3 = new Film("name3", "descriptionFilm3", releaseDate3, 140);
        controller.addFilm(film3);

        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.getFilmService().getUserStorage().createUser(user1);
        LocalDate birthday2 = LocalDate.of(2002, 10, 10);
        User user2 = new User("name@yandex.ru", "Login2", birthday2);
        controller.getFilmService().getUserStorage().createUser(user2);
        LocalDate birthday3 = LocalDate.of(2003, 12, 12);
        User user3 = new User("name@list.ru", "Login3", birthday3);
        controller.getFilmService().getUserStorage().createUser(user3);

        controller.addLike(film3.getId(), user1.getId());
        controller.addLike(film3.getId(), user2.getId());
        controller.addLike(film3.getId(), user3.getId());

        controller.addLike(film2.getId(), user1.getId());
        controller.addLike(film2.getId(), user3.getId());

        List<Film> filmsPopular = controller.findPopularFilms(3);

        assertEquals(filmsPopular.get(0).getName(), "name3", "Поля не совпадают.");
        assertEquals(filmsPopular.get(1).getDuration(), 130, "Поля не совпадают");
        assertEquals(filmsPopular.get(2).getDescription(), "descriptionFilm1", "Поля не совпадают");
        assertEquals(filmsPopular.size(), 3, "Размер списка не совпадает.");
    }

    /**
     * Проверка обработки получения списка популярных фильмов с некорректным count
     * @throws ValidationException
     */
    @Test
    void shouldBeNotGetListPopularFilmsWhenCountIncorrect() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("name1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate releaseDate2 = LocalDate.of(2002, 10, 10);
        Film film2 = new Film("name2", "descriptionFilm2", releaseDate2, 130);
        controller.addFilm(film2);

        LocalDate birthday = LocalDate.of(2000, 11, 11);
        User user1 = new User("name@mail.ru", "LoginCorrect", birthday);
        controller.getFilmService().getUserStorage().createUser(user1);
        LocalDate birthday2 = LocalDate.of(2002, 10, 10);
        User user2 = new User("name@yandex.ru", "Login2", birthday2);
        controller.getFilmService().getUserStorage().createUser(user2);

        controller.addLike(film2.getId(), user1.getId());
        controller.addLike(film2.getId(), user2.getId());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.findPopularFilms(-2);
        });
        assertNotNull(thrown.getMessage());

        assertEquals(thrown.getMessage(), "Лимит списка не может быть отрицательным или нулевым.",
                "Сообщения об ошибке не совпадают");
    }
}