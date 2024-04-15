# REST API для управления книгами с использованием Spring Boot + кэширование в Redis
Приложение предоставляет следующие функции:
* создать книгу
* обновить информацию о книге
* удалить книгу по ID
* найти одну книгу по её названию и автору
* найти список книг по имени категории

## Запуск приложения
### Создание и запуск docker контейнеров
```
cd docker
docker compose up
```
### Запуск проетка или `./gradlew bootRun`
### Запуск тестов
```
./gradlew test
```

## Эндпойнты
* **Новая книга**

    Обязательные параметры: title, author, category
```
POST http://localhost:8081/api/books

Тело:
{
"title":"Воскресение",
"author":"Толстой",
"category":"роман"
}
```
* **Изменить книгу**

    Обязательные параметры: id, title, author, category
```
PUT http://localhost:8081/api/books/{id}

{
"title":"Воскресение",
"author":"Л.Толстой",
"category":"Роман"
}
```
* **Удалить книгу**

    Обязательные параметры: id
```
DELETE http://localhost:8081/api/books/{id}}
```
* **Найти книгу по названию и автору**

    Обязательные параметры: title, author
```
GET http://localhost:8081/api/books?title=Воскресение&author=Л.Толстой
```
* **Найти книги по категории**

    Обязательные параметры: name
```
GET http://localhost:8081/api/books/by-category?name=Роман
```
