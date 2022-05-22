-- ACTORS
INSERT INTO actors (`id`, `name`) VALUES (1, 'Jim Carrey');
INSERT INTO actors (`id`, `name`) VALUES (2, 'Renee Zellweger');
INSERT INTO actors (`id`, `name`) VALUES (3, 'Cameron Diaz');

-- MOVIES
INSERT INTO movies (`id`, `title`, `year`, `genre`) VALUES (1, 'Me, Myself and Irene', 2000, 'COMEDY');
INSERT INTO movies (`id`, `title`, `year`, `genre`, `poster`) VALUES (2, 'The Mask', 1994, 'COMEDY', 'https://pics.filmaffinity.com/La_m_scara-746858956-large.jpg');

-- MOVIES AND ACTORS
INSERT INTO movies_actors (`movie_id`, `actor_id`) VALUES (1, 1);
INSERT INTO movies_actors (`movie_id`, `actor_id`) VALUES (1, 2);
INSERT INTO movies_actors (`movie_id`, `actor_id`) VALUES (2, 1);
INSERT INTO movies_actors (`movie_id`, `actor_id`) VALUES (2, 3);

-- MOVIES AND LANGUAGES
INSERT INTO movies_languages (`movie_id`, `language`) VALUES (1, 'ENGLISH');
INSERT INTO movies_languages (`movie_id`, `language`) VALUES (1, 'SPANISH');
INSERT INTO movies_languages (`movie_id`, `language`) VALUES (2, 'ENGLISH');
INSERT INTO movies_languages (`movie_id`, `language`) VALUES (2, 'FRENCH');
INSERT INTO movies_languages (`movie_id`, `language`) VALUES (2, 'OTHER');