package ru.yandex.practicum.filmorate.model;

public class ErrorResponse {
    public String error;

    /**
     * Класс объекта для универсального формата ошибки
     * @param error
     */
    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
