package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.id.FilmId;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utilit.WarnAndThrowException;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final FilmId filmId = new FilmId();
    private static final LocalDate FIRST_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private final UserStorage userStorage;
    private  String nameUser;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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


    public Film create(Film film) {
        if (films.containsKey(film.getId())) {
            WarnAndThrowException.logWarnAndThrowException("Фильм уже существует");
        }
        validate(film);
        film.setId(filmId.getFilmId());
        log.info("Добавлен фильм: {}", film);
        films.put(film.getId(), film);
        return film;
    }


    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            String message = String.format("Фильм не существует: %s", film.getName());
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
        validate(film);
        log.info("Обновлен фильм: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    public List<Film> findAll() {
        log.info("Доступно фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    public Film getFilm(Long id) {
        if (!films.containsKey(id)) {
            WarnAndThrowException.logAndThrowExceptionIfIdFilmNotExist(id);
        }

        log.info("Выдан фильм: {}", films.get(id).getName());
        return films.get(id);
    }

    public void addLike(Long id, Long userId) {
        if (!films.containsKey(id)) {
            WarnAndThrowException.logAndThrowExceptionIfIdFilmNotExist(id);
            String message = String.format("Не существует пользователь с id=%d", userId);
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
        nameUser=userStorage.getUser(userId).getName();
        films.get(id).getLikes().add(userId);
        log.info("Пользователь {} добавил лайк фильму {}",
                nameUser,
                films.get(id).getName());
    }

    public void removeLike(Long id, Long userId) {
        if (!films.containsKey(id)) {
            WarnAndThrowException.logAndThrowExceptionIfIdFilmNotExist(id);
            String message = String.format("Не существует пользователь с id=%d", userId);
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
                nameUser=userStorage.getUser(userId).getName();
                films.get(id).getLikes().remove(userId);
                log.info("Пользователь {} удалил лайк фильму {}",
                        nameUser,
                        films.get(id).getName());
    }

    public List<Film> getPopularFilms(Long count) {
        return films.values().stream().sorted((p0, p1) -> {
            int comp = Integer.compare(p0.getLikes().size(), p1.getLikes().size());
            return comp * -1;
        }).limit(count).collect(Collectors.toList());
    }
}