# ⭕ Tic-Tac-Toe API

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1-success)
![Spring Web](https://img.shields.io/badge/Spring_Web-7-green)
![Spring Security](https://img.shields.io/badge/Spring_Security-7-green)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-4.1-yellowgreen)
![Hibernate](https://img.shields.io/badge/Hibernate-7.4-59666C)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue)
![Auth](https://img.shields.io/badge/Auth-JWT-red)
![Passwords](https://img.shields.io/badge/Passwords-BCrypt-yellow)
![Build](https://img.shields.io/badge/Build-Gradle_9-important)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![CI](https://img.shields.io/badge/CI-GitHub_Actions-2088FF)
![OpenAPI](https://img.shields.io/badge/OpenAPI-3-Swagger-85EA2D)

---

# 📖 О проекте

**Tic-Tac-Toe API** — REST-сервис для игры в крестики-нолики: два режима — партия против другого игрока (PvP) и против компьютера.

Проект реализован на **Spring Boot** с многослойной архитектурой, JWT-аутентификацией, разделением слоёв Web / Domain / Datasource и чистой доменной моделью (enum `Cell` вместо «магических чисел»).

---

# 🚀 Возможности

- регистрация пользователя;
- авторизация по JWT (access + refresh токены);
- партия **два игрока** (PvP) — присоединение к ожидающей игре;
- партия **против компьютера** (минимакс);
- автоматический ход компьютера;
- история завершённых игр;
- лидерборд по соотношению побед;
- валидация входящих запросов (Bean Validation);
- централизованная обработка исключений;
- **документация API в Swagger UI / OpenAPI 3** (с JWT-схемой);
- контейнеризация Docker Compose и CI через GitHub Actions.

---

# 🏛 Архитектура

Проект построен по классической многослойной архитектуре.

```
            Client
              │
              ▼
       Web (Controller / DTO / Mapper)
              │
              ▼
       Domain (Service / Model / Exceptions)
              │
              ▼
       Datasource (Repository / Entity)
              │
              ▼
         PostgreSQL
```

Каждый слой отвечает только за свою область ответственности:

- **Web** — REST-контроллеры, DTO, мапперы DTO ↔ Domain, обработка ошибок;
- **Domain** — бизнес-логика, доменные модели (`Game`, `Board`, enum `Cell`, `GameStatus`) и исключения;
- **Datasource** — JPA-сущности, репозитории, мапперы Entity ↔ Domain.

В доменной модели доска типизирована как `Cell[][]` (`EMPTY`, `X`, `O`) — недопустимые значения невозможны на уровне типов, а на границах слоёв происходит конвертация в/из `int` через `Cell.fromValue` / `Cell.toValue` (формат API и БД остаётся `0/1/2`).

---

# 🔒 Безопасность

- **JWT-аутентификация** (jjwt 0.12, HMAC-SHA): после логина выдаются `accessToken` и `refreshToken`;
- **refresh-механизм**: `POST /token/access` и `POST /token/refresh` ротируют пару токенов (общая логика `rotateRefreshToken`);
- **BCrypt** для хэширования паролей (`PasswordEncoder`);
- цепочка `SecurityFilterChain` + `AuthFilter`: публичные только `/register`, `/login`, `/token/access`, `/token/refresh`;
- унифицированные ответы об ошибках авторизации: **401** — не аутентифицирован, **403** — нет доступа (JSON `ApiError`);
- статус «Stateless»: сессий нет, пользователь определяется из JWT; principal — UUID пользователя.

---

# 📡 REST API

Все эндпоинты, кроме аутентификации, требуют заголовок `Authorization: Bearer <token>`.

## Authentication (публичные)

```
POST /register         — регистрация { login, password }  → UUID
POST /login            — авторизация { login, password }  → { accessToken, refreshToken }
POST /token/access     — новый access-токен { refreshToken }
POST /token/refresh    — ротация пары { refreshToken }
```

## User (требуют аутентификации)

```
GET /user/me      — информация о текущем пользователе
GET /user/{uuid}  — информация о пользователе по id
```

## Game (требуют аутентификации)

```
POST   /game/create?vsComputer=true|false — создать игру
GET    /game/available                    — игры, ожидающие второго игрока
POST   /game/{uuid}/join                  — присоединиться к игре (PvP)
POST   /game/{uuid}                       — сделать ход
GET    /game/{uuid}                       — получить игру
GET    /game/history                      — завершённые игры пользователя
GET    /game/leaderboard?n=10             — топ игроков по победам
```

Доска — массив `3x3`, значение клетки: `0` — пусто, `1` — X, `2` — O:

```json
{
  "id": "<uuid>",
  "board": { "grid": [[1, 2, 0], [0, 0, 0], [0, 0, 0]] }
}
```

---

# 🤖 Партия против компьютера

Компьютер играет за **O** и выбирает ход алгоритмом **минимакс**: `findBestMove` перебирает пустые клетки, `miniMax` оценивает дерево игры по счёту `10 - depth` / `depth - 10`, а `checkWinner` возвращает победившую клетку (`Cell.X` / `Cell.O`). Стейт доски защищён от подделки: сервер сверяет, что клиент изменил ровно одну пустую клетку своим маркером (`validateBoard`).

---

# ❗ Обработка ошибок

Централизованный обработчик `@RestControllerAdvice` + единый формат `ApiError`:

```json
{ "status": 404, "error": "Not Found", "message": "...", "timestamp": "..." }
```

Маппинг:

- `GameNotFoundException` → **404**
- `IllegalMoveException`, `IllegalArgumentException` → **400**
- `MethodArgumentNotValidException` → **400** (детали по каждому полю)
- `ResponseStatusException` → его статус
- `HttpMessageNotReadableException` (битый JSON) → **400**
- `NoResourceFoundException` (нет роута) → **404**
- непредвиденные исключения → **500**

---

# ⚙️ Используемые технологии

## Backend

- Java 25
- Spring Boot 4.1
- Spring Web / Spring MVC
- Spring Security 7
- Spring Data JPA / Hibernate 7.4
- Bean Validation (jakarta.validation)

## База данных

- PostgreSQL 18
- `ddl-auto=update` + конвертер доски в JSON (`TEXT`)

## Безопасность

- JWT (jjwt 0.12) — access/refresh
- BCrypt

## Документация

- Swagger UI / OpenAPI 3 (springdoc-openapi, схема JWT `bearerAuth`)

## Сборка и инструменты

- Gradle 9 (wrapper)
- Docker Compose
- GitHub Actions CI

---

# 🗄 База данных

Таблицы создаются автоматически при старте приложения (`spring.jpa.hibernate.ddl-auto=update`):

- `users` — пользователи (логин, BCrypt-хэш);
- `user_roles` — роли;
- `games` — партии (статус, игроки, победитель, текущий ход);
- `boards` — доски (сериализованный JSON `3x3`).

Секреты и доступы к БД задаются переменными окружения (с fallback для локальной разработки):

```
DB_URL        # jdbc:postgresql://localhost:5432/tictactoe
DB_USERNAME
DB_PASSWORD
JWT_ACCESS_SECRET   # ≥ 32 байт
JWT_REFRESH_SECRET  # ≥ 32 байт
```

---

# 🐳 Запуск через Docker Compose

```bash
docker compose up -d
```

Поднимает **PostgreSQL 18** (volume `pgdata`, healthcheck) и приложение на `http://localhost:8080`. Схема создаётся автоматически.

---

# ▶️ Локальный запуск

### 1. Клонировать проект

```bash
git clone https://github.com/Yangklounada/TicTacToe.git
cd Tic_Tacoe
```

### 2. Запустить PostgreSQL

Локальный сервер (например, через Homebrew или Docker):

```bash
docker compose up -d db
```

### 3. Настроить переменные окружения (опционально)

Если окружение не задано — используются dev-значения по умолчанию из `application.properties`.

### 4. Запустить приложение

```bash
./gradlew bootRun
```

Приложение будет доступно на `http://localhost:8080`.

---

# ✅ CI

GitHub Actions (`build`-джоб):

- `push` в `main` и `pull_request`;
- JDK 25 (Temurin), кэш Gradle;
- `./gradlew build` (компиляция + тесты);
- публикация артефакта тест-отчётов.

---

# 📑 Swagger

После запуска проекта документация доступна по адресу:

```
http://localhost:8080/swagger-ui/index.html
```

Спецификация OpenAPI: `http://localhost:8080/v3/api-docs`. Для protected-эндпоинтов нажми **Authorize** и вставь `Bearer <accessToken>`.

---

# 🎯 Особенности проекта

✔ JWT Authentication (access + refresh)  
✔ Role Based Security  
✔ BCrypt Password Hashing  
✔ REST API  
✔ PostgreSQL  
✔ Docker Compose  
✔ GitHub Actions CI  
✔ Layered Architecture (Web / Domain / Datasource)  
✔ Typed Domain Model (enum `Cell`)  
✔ Minimax AI  
✔ DTO + Mapper Layer  
✔ Bean Validation  
✔ Centralized Exception Handling  
✔ Secrets через env-переменные  
✔ Defensive Board Copying  
✔ Swagger UI / OpenAPI 3