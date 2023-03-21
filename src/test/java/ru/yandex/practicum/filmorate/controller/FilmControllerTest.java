package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест класс для проверки обработки эндпоинтов класса FilmController
 */
class FilmControllerTest {

    FilmController controller = new FilmController();

    /**
     * Проверка обработки сохранения фильма при пустом теле запроса
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeEmptyMupWhenRequestBodyEmpty() throws ValidationException {

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.addFilm(null);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.films.isEmpty(), "Фильм сохранен");
    }

    /**
     * Проверка обработки сохранения фильма при совпадении названия с имеющимся в коллекции
     *
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

        assertEquals(controller.films.size(), 1, "Фильм 2 сохранен");
    }

    /**
     * Проверка обработки сохранения фильма при добавлении фильма с пустым названием
     *
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

        assertTrue(controller.films.isEmpty(), "Фильм сохранен");
    }

    /**
     * Проверка обработки сохранения фильма при добавлении фильма с описанием более 200 символов
     *
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

        assertTrue(controller.films.isEmpty(), "Фильм сохранен");
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

        assertTrue(controller.films.isEmpty(), "Фильм сохранен");
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

        assertTrue(controller.films.isEmpty(), "Фильм сохранен");
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

        assertTrue(controller.films.isEmpty(), "Фильм обновлен");
    }

    /**
     * Проверка обработки обновления фильма при некорректном id
     *
     * @throws ValidationException
     */
    @Test
    void shouldNotBeUpdateWhenIdIncorrect() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film = new Film("nameFilm1", "descriptionFilm1", releaseDate, 120);
        film.setId(9999);

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            controller.updateFilm(film);
        });
        assertNotNull(thrown.getMessage());

        assertTrue(controller.films.isEmpty(), "Фильм обновлен");
    }

    /**
     * Проверка получения списка всех фильмов при запросе GET
     *
     * @throws ValidationException
     */
    @Test
    void shouldBeSavedListWhenRequestGet() throws ValidationException {
        controller.films.clear();
        LocalDate releaseDate = LocalDate.of(2000, 11, 11);
        Film film1 = new Film("nameFilm1", "descriptionFilm1", releaseDate, 120);
        controller.addFilm(film1);
        LocalDate releaseDate2 = LocalDate.of(2001, 12, 12);
        Film film2 = new Film("nameFilm2", "descriptionFilm2", releaseDate2, 140);
        controller.addFilm(film2);

        List<Film> filmsSave = new ArrayList<>(controller.findAllFilms());

        assertEquals(filmsSave.get(0).getName(), "nameFilm1", "Поля не совпадают.");
        assertEquals(filmsSave.get(1).getDuration(), 140, "Поля не совпадают");
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

        assertEquals(controller.films.get(film1.getId()).getDescription(), "descriptionFilm1", "Фильм обновлен");
        assertEquals(controller.films.get(film1.getId()).getDuration(), 120, "Фильм обновлен");
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

        assertEquals(controller.films.get(film1.getId()).getDescription(), "descriptionFilm1", "Фильм обновлен");
        assertEquals(controller.films.get(film1.getId()).getDuration(), 120, "Фильм обновлен");
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

        assertEquals(controller.films.get(film1.getId()).getDescription(), "descriptionFilm1", "Фильм обновлен");
        assertEquals(controller.films.get(film1.getId()).getDuration(), 120, "Фильм обновлен");
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

        assertEquals(controller.films.get(film1.getId()).getDescription(), "descriptionFilm1", "Фильм обновлен");
        assertEquals(controller.films.get(film1.getId()).getDuration(), 120, "Фильм обновлен");
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

        assertEquals(controller.films.get(film1.getId()).getDescription(), "descriptionFilmUpdate",
                "Фильм не обновлен");
        assertEquals(controller.films.get(film1.getId()).getDuration(), 140, "Фильм не обновлен");
    }
}