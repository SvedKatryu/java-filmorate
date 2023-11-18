create table if not exists mpa (
    mpa_id      int AUTO_INCREMENT primary key,
    name        varchar(255)  NOT NULL,
    description varchar(255) NOT NULL
);

create table if not exists films (
    film_id      int AUTO_INCREMENT primary key,
    name         varchar(255) NOT NULL,
    description  varchar(255),
    release_date date  NOT NULL,
    duration     int NOT NULL,
    mpa_id       int REFERENCES mpa (mpa_id) ON delete RESTRICT
);

create table if not exists film_genres (
    film_id  int NOT NULL,
    genre_id int NOT NULL,
    PRIMARY KEY (film_id, genre_id)
);

create table if not exists genres (
    genre_id int AUTO_INCREMENT primary key,
    name     varchar(255) NOT NULL
);

create table if not exists users (
    user_id  int AUTO_INCREMENT primary key,
    email    varchar(255) NOT NULL,
    login    varchar(255) NOT NULL,
    name     varchar(255),
    birthday date NOT NULL
);

create table if not exists likes (
    film_id int NOT NULL,
    user_id int NOT NULL,
    PRIMARY KEY (film_id, user_id)
);

create table if not exists friendships (
    user_id    int NOT NULL,
    friends_id int NOT NULL,
    PRIMARY KEY (user_id, friends_id)
);

delete from film_genres;
delete from friendships;
delete from FILMS;
delete from likes;
delete from users;
alter table users alter COLUMN user_id RESTART with 1;
alter table films alter COLUMN film_id RESTART with 1;