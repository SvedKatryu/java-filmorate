# java-filmorate
![ER-diagram](ER-diagram.png)

* _users_ таблица пользователей
* _friends_ таблица для отображения связей друзей 
* _likes_ таблица для отображения связей лайков пользователей и фильмов
* _films_ таблица фильмов 
* _mpa_ таблица mpa-рейтингов фильмов 
* _genres_ таблица жанров фильмов
* _film_genres_ соединительная таблица для связи многие ко многим таблиц _films_ и _genres_

Получим всех пользователей:
```
SELECT *  
FROM users;
```

Получим все фильмы:
```
SELECT *  
FROM films;
```

Получим топ 5 фильмов:
```
SELECT f.name, COUNT(l.user_id) AS count_likes
FROM films AS f
LEFT JOIN likes AS l ON f.id = l.film_id
GROUP BY f.id
ORDER BY count_likes DESC
LIMIT 5;
```
