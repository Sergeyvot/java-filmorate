# java-filmorate
Template repository for Filmorate project.

![Диаграмма](/resources/ER_filmorate.png)

## Получение всех пользователей
SELECT *
FROM user;

## Получение пользователя с конкретным id=<N>
SELECT *
FROM user
WHERE user_id=<N>;

## Получение списка всех друзей пользователя с id=<N>
SELECT *
FROM user
WHERE user_id IN (SELECT friend_id
                  FROM friends
                  WHERE user_id=<N>
                  GROUP BY user_id);

## Получение списка общих друзей пользователей user1_id и user2_id
SELECT *
FROM user
WHERE user_id IN (SELECT fr1.friend_id
                  FROM friends AS fr1
                  INNER JOIN friends AS fr2 ON fr1.friend_id=fr2.friend_id
                  WHERE fr1.user_id=<user1_id> 
                    AND fr2.user_id=<user2_id>
                  GROUP BY user_id)

## Получение всех фильмов
SELECT *
FROM film;

## Получение фильма с конкретным id=<N>
SELECT *
FROM film
WHERE film_id=<N>;

## Получение ТОП фильмов с ограничением списка <count>
SELECT f.name,
       l.count_likes
FROM film AS f
INNER JOIN (SELECT film_id,
                   COUNT(user_id) AS count_likes
                   FROM likes
                   GROUP BY film_id
                   ORDER BY COUNT(user_id) DESC
                    LIMIT <count>) AS l ON l.film_id=f.film_id;

