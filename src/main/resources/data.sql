DELETE FROM public.genre;
DELETE FROM public.mpa;
DELETE FROM public.users;
DELETE FROM public.films;
DELETE FROM public.likes;
DELETE FROM public.friends;


INSERT INTO public.genre (name)
VALUES ('Комедия');
INSERT INTO public.genre (name)
VALUES ('Драма');
INSERT INTO public.genre (name)
VALUES ('Мультфильм');
INSERT INTO public.genre (name)
VALUES ('Триллер');
INSERT INTO public.genre (name)
VALUES ('Документальный');
INSERT INTO public.genre (name)
VALUES ('Боевик');


INSERT INTO public.mpa (name)
VALUES ('G');
INSERT INTO public.mpa (name)
VALUES ('PG');
INSERT INTO public.mpa (name)
VALUES ('PG-13');
INSERT INTO public.mpa (name)
VALUES ('R');
INSERT INTO public.mpa (name)
VALUES ('NC-17');
