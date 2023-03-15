
![db-filmorate.png](https://raw.githubusercontent.com/Eleonora-Y/java-filmorate/main/db-filmorate.png)

Получение всех фильмов:

SELECT film_id FROM films;


Получение всех пользователей:

SELECT user_id FROM users;


Получение ТОП-5 наиболее популярных фильмов:

SELECT film_id FROM( SELECT film_id, COUNT(user_id) AS c FROM likes GROUP BY film_id ORDER BY c DESC LIMIT 5) AS a;
