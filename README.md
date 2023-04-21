# java-filmorate
Template repository for Filmorate project.

![Диаграмма](/resources/ER_filmorate.png)

## Получение всех пользователей
SELECT *
FROM users;

## Получение пользователя с конкретным id=<N>
SELECT *
FROM users
WHERE user_id=<N>;

## Получение списка всех друзей пользователя с id=<N>
SELECT *
FROM users
WHERE user_id IN (SELECT unfriend_id AS friend_id
                  FROM unconfirmed_friends
                  WHERE user_id=<N>
                  GROUP BY user_id
                  UNION
                  SELECT friend_id
                  FROM friends
                  WHERE user_id=<N>
                  GROUP BY user_id);

## Получение списка общих друзей пользователей user1_id и user2_id
SELECT *
FROM users
WHERE user_id IN (SELECT unfriend_id AS friend_id
                  FROM unconfirmed_friends
                  WHERE user_id=user1_id
                     AND unfriend_id IN (SELECT unfriend_id AS friend_id
                                         FROM unconfirmed_friends
                                         WHERE user_id=user2_id
                                         GROUP BY user_id
                                         UNION
                                         SELECT friend_id
                                         FROM friends
                                         WHERE user_id=user2_id
                                         GROUP BY user_id)
                  GROUP BY user_id
                  UNION
                  SELECT friend_id
                  FROM friends
                  WHERE user_id=user1_id
                     AND friend_id IN (SELECT unfriend_id AS friend_id
                                       FROM unconfirmed_friends
                                       WHERE user_id=user2_id
                                       GROUP BY user_id
                                       UNION
                                       SELECT friend_id
                                       FROM friends
                                       WHERE user_id=user2_id
                                       GROUP BY user_id)
                  GROUP BY user_id)

## Получение всех фильмов
SELECT *
FROM films;

## Получение фильма с конкретным id=<N>
SELECT *
FROM films
WHERE film_id=<N>;

## Получение ТОП фильмов с ограничением списка <count>
SELECT f.name,
       l.count_likes
FROM films AS f
INNER JOIN (SELECT film_id,
                   COUNT(user_id) AS count_likes
                   FROM likes
                   GROUP BY film_id
                   ORDER BY COUNT(user_id) DESC
                    LIMIT <count>) AS l ON l.film_id=f.film_id;

