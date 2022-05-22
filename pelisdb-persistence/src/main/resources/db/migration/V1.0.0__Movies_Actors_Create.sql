-- DROP TABLES
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS actors;
DROP TABLE IF EXISTS movies_actors;
DROP TABLE IF EXISTS movies_languages;

-- CREATE TABLES
CREATE TABLE movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(250) NOT NULL UNIQUE,
    year INT NOT NULL,
    genre VARCHAR(50) NOT NULL,
    poster VARCHAR(350)
);

CREATE TABLE actors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL
);

CREATE TABLE movies_actors (
    movie_id INT NOT NULL,
    actor_id INT NOT NULL,
    FOREIGN KEY(movie_id) REFERENCES movies(id),
    FOREIGN KEY(actor_id) REFERENCES actors(id)
);

CREATE TABLE movies_languages (
    movie_id INT NOT NULL,
    language VARCHAR(50) NOT NULL,
    FOREIGN KEY(movie_id) REFERENCES movies(id)
);

-- INDEXES
CREATE INDEX idx_movies_title ON movies(title);
CREATE INDEX idx_actors_name ON actors(name);