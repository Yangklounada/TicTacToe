# Spring Game Service API

REST API игрового сервиса, разработанный на **Java** с использованием **Spring Boot**.

Проект демонстрирует разработку backend-приложения с использованием многослойной архитектуры, разделением бизнес-логики, REST API и принципов объектно-ориентированного проектирования.

---

# Возможности

На текущий момент реализовано:

- REST API для работы с игровыми сущностями;
- многослойная архитектура (Controller → Service → Repository);
- разделение Domain, Web и Datasource слоев;
- использование DTO и Mapper;
- Dependency Injection;
- обработка HTTP-запросов;
- работа с JSON;
- разделение бизнес-логики и слоя доступа к данным.

---

# Архитектура проекта

```
                Client
                   │
                   ▼
          REST Controller
                   │
                   ▼
             Service Layer
                   │
                   ▼
           Repository Layer
                   │
                   ▼
              Data Source
```

Каждый слой отвечает только за собственную область ответственности.

- **Web** — REST API и обработка HTTP-запросов;
- **Service** — бизнес-логика приложения;
- **Repository** — взаимодействие с хранилищем данных;
- **Datasource** — модели хранения данных;
- **Domain** — модели предметной области.

---

# Используемые технологии

- Java
- Spring Boot
- Spring Web
- Gradle
- REST API
- Dependency Injection

---

# Структура проекта

```
src
└── main
    └── java
        ├── app
        ├── datasource
        │   ├── mapper
        │   ├── model
        │   └── repository
        ├── di
        ├── domain
        │   ├── model
        │   ├── repository
        │   └── service
        └── web
            ├── controller
            ├── dto
            └── mapper
```

---

# Основной функционал

### REST API

- обработка HTTP GET запросов;
- обработка HTTP POST запросов;
- обработка HTTP PUT запросов;
- обработка HTTP DELETE запросов;
- обмен данными в формате JSON.

### Архитектура

- разделение проекта на слои;
- сервисный слой;
- Dependency Injection;
- использование DTO;
- преобразование DTO ↔ Domain через Mapper.

---

# Чему я научился

Во время разработки проекта были изучены и применены:

- Spring Boot;
- Spring MVC;
- построение REST API;
- Dependency Injection;
- Controller / Service / Repository Architecture;
- DTO и Mapper;
- организация backend-приложений;
- разделение ответственности между слоями приложения.

---

# Планы по развитию

В дальнейшем планируется расширение проекта следующими возможностями:

- регистрация пользователей;
- аутентификация и авторизация;
- Spring Security;
- JWT-аутентификация;
- подключение PostgreSQL;
- Spring Data JPA и Hibernate;
- Docker;
- Swagger / OpenAPI;
- централизованная обработка исключений;
- логирование;
- тестирование REST API.

---

# Запуск проекта

```bash
git clone https://github.com/<username>/<repository>.git

cd <repository>

./gradlew bootRun
```

После запуска приложение будет доступно по адресу

```
http://localhost:8080
```
