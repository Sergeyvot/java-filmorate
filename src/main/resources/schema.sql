CREATE TABLE IF NOT EXISTS users
(
    id int generated by default as identity primary key,
    name varchar(100),
    email varchar(100) NOT NULL,
    login varchar(100) NOT NULL,
    birthday date
    );

ALTER TABLE users ALTER COLUMN id RESTART WITH 1;

CREATE TABLE IF NOT EXISTS mpa
(
    id int primary key,
    name varchar(100) NOT NULL
);

ALTER TABLE mpa ALTER COLUMN id int generated by default as identity;

CREATE TABLE IF NOT EXISTS genre
(
    id int primary key,
    name varchar(100) NOT NULL
);

ALTER TABLE genre ALTER COLUMN id int generated by default as identity;

CREATE TABLE IF NOT EXISTS films
(
    id int generated by default as identity primary key,
    name varchar(200) NOT NULL,
    description varchar(200),
    release_date date,
    duration int CHECK duration > 0,
    mpa_id int,
    CONSTRAINT fk_mpa_id
    FOREIGN KEY (mpa_id)
    REFERENCES mpa (id) ON DELETE CASCADE
    );

ALTER TABLE films ALTER COLUMN id RESTART WITH 1;

ALTER TABLE films ADD CONSTRAINT IF NOT EXISTS fk_mpa_id_cascade FOREIGN KEY (mpa_id)
    REFERENCES mpa (id) ON DELETE CASCADE;
ALTER TABLE films DROP CONSTRAINT IF EXISTS fk_mpa_id;


CREATE TABLE IF NOT EXISTS genre_film
(
    film_id int REFERENCES films (id),
    genre_id int REFERENCES genre (id),
    primary key (film_id, genre_id)
    );

ALTER TABLE genre_film DROP CONSTRAINT IF EXISTS CONSTRAINT_C;
ALTER TABLE genre_film DROP CONSTRAINT IF EXISTS CONSTRAINT_CA;

ALTER TABLE genre_film ADD CONSTRAINT IF NOT EXISTS fk_film_id_cascade FOREIGN KEY (film_id)
    REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE genre_film ADD CONSTRAINT IF NOT EXISTS fk_genre_id_cascade FOREIGN KEY (genre_id)
    REFERENCES genre (id) ON DELETE CASCADE;


ALTER TABLE genre_film DROP CONSTRAINT IF EXISTS constraint_unique_genre;


CREATE TABLE IF NOT EXISTS likes
(
    film_id int REFERENCES films (id),
    user_id int REFERENCES users (id),
    primary key (film_id, user_id)
    );

ALTER TABLE likes DROP CONSTRAINT IF EXISTS constraint_45;
ALTER TABLE likes DROP CONSTRAINT IF EXISTS constraint_451;

ALTER TABLE likes ADD CONSTRAINT IF NOT EXISTS fk_film_id_likes_cascade FOREIGN KEY (film_id)
    REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE likes ADD CONSTRAINT IF NOT EXISTS fk_user_id_likes_cascade FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS friends
(
    user_id int REFERENCES users (id),
    friend_id int REFERENCES users (id),
    primary key (user_id, friend_id)
    );

ALTER TABLE friends DROP CONSTRAINT IF EXISTS constraint_7;
ALTER TABLE friends DROP CONSTRAINT IF EXISTS constraint_70;


ALTER TABLE friends ADD CONSTRAINT IF NOT EXISTS fk_user_id_friends_cascade FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE friends ADD CONSTRAINT IF NOT EXISTS fk_friend_id_friends_cascade FOREIGN KEY (friend_id)
    REFERENCES users (id) ON DELETE CASCADE;

