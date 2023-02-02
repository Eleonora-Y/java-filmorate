package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.utilit.WarnAndThrowException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.id.FilmId;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private final FilmId filmId = new FilmId();
    private static final LocalDate FIRST_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    public void validate(Film film) {

        if (film.getName().isEmpty() || film.getName() == null) {
            WarnAndThrowException.logWarnAndThrowException("Название пустое");
        }
        if (film.getDescription().length() == 0 || film.getDescription().length() > 200) {
            WarnAndThrowException.logWarnAndThrowException("Нет описания,или превышена длина (200 символов)");
        }
        if (film.getReleaseDate().isBefore(FIRST_RELEASE_DATE)) {
            WarnAndThrowException.logWarnAndThrowException("Дата релиза — не должна быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() < 0 || film.getDuration() == 0) {
            WarnAndThrowException.logWarnAndThrowException("Продолжительность фильма должна быть положительной");
        }
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            WarnAndThrowException.logWarnAndThrowException("Фильм уже существует");
        }
        validate(film);
        film.setId(filmId.getFilmId());
        log.info("Добавлен фильм: {}", film);
        films.put(film.getId(), film);
        return film;
    }


    @PutMapping
    public Film update(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            WarnAndThrowException.logWarnAndThrowException("Фильм не существует");
        }
        validate(film);
        log.info("Обновлен фильм: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Доступно фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    public Map<Integer, Film> getFilms() {
        return films;
    }

}