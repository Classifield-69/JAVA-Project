# JAVA-Project
# GameCollection

Уеб приложение за видео игри: каталог с игри, лична колекция (**Owned** / **Wishlist**) по платформи и ревюта с оценка от 1 до 10.

Проект по дисциплината **JAVA технологии**: Java уеб приложение с база данни, backend, frontend, защита със Spring Security и вход с Google.

---

## Възможности

- **Каталог с игри:** списък с търсене по име и страници, детайли (описание, платформи, жанрове, средна оценка, ревюта).
- **Потребители:** регистрация с имейл и парола или **вход с Google** (OAuth2).
- **Колекция:** добавяне на игра като OWNED или WISHLIST за конкретна платформа, смяна на типа, премахване.
- **Ревюта:** едно ревю на игра от потребител, оценка 1–10; редакция само от автора, изтриване от автора или от ADMIN.

## Технологии

| Част | Технология |
|---|---|
| Backend | Java 21, Spring Boot, Spring Web, Spring Data JPA, Spring Security, OAuth2 Client, Validation, Flyway, Lombok |
| Защита | BCrypt, JWT (jjwt), роли USER/ADMIN, заключване след грешни опити, CORS |
| База данни | PostgreSQL, хостната онлайн в **Neon** |
| Frontend | Vue 3 + Vite, Vue Router, Pinia, Axios |
| Инструменти | VS Code, Postman, DBeaver, Git/GitHub |

---

## Структура

```
JAVA-Project/
├── package.json          ← npm start: пуска backend и frontend заедно
├── backend/              ← Spring Boot (порт 8080)
│   ├── .env              ← тайните настройки (не е в Git)
│   └── src/main/
│       ├── java/com/gamecollection/
│       │   ├── config/       ← SecurityConfig, PasswordConfig
│       │   ├── controller/   ← REST endpoint-и
│       │   ├── dto/          ← заявки и отговори (records)
│       │   ├── entity/       ← JPA entities
│       │   ├── exception/    ← собствени грешки + GlobalExceptionHandler
│       │   ├── repository/   ← Spring Data JPA
│       │   ├── security/     ← JWT, Google вход, заключване
│       │   └── service/      ← бизнес логиката
│       └── resources/
│           ├── application.yml
│           └── db/migration/ ← Flyway миграции (схема + seed данни)
└── frontend/             ← Vue 3 (порт 5173)
    ├── .env              ← VITE_API_URL
    ├── public/covers/    ← кориците на игрите
    └── src/
        ├── api.js        ← Axios + токенът на всяка заявка
        ├── router/       ← адреси + guard
        ├── stores/       ← Pinia: кой е влязъл
        └── views/        ← страниците
```

---

## Изисквания

- **Java 21** (JDK)
- **Node.js** 22 или по-нов (с npm)
- Акаунт в **Neon** (PostgreSQL) и OAuth клиент в **Google Cloud** (за входа с Google)

Maven не е нужно да е инсталиран отделно: проектът ползва `./mvnw`.

---

## Настройка

### 1. `backend/.env`

```properties
# База данни (Neon, JDBC адрес без потребител и парола в него)
DB_URL=jdbc:postgresql://ep-XXXX.eu-central-1.aws.neon.tech/neondb?sslmode=require
DB_USERNAME=neondb_owner
DB_PASSWORD=...

# JWT (генерира се с: openssl rand -base64 32)
JWT_SECRET=...

# Google OAuth2
GOOGLE_CLIENT_ID=...apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=GOCSPX-...
```

В Google Cloud Console, като **Authorized redirect URI** на OAuth клиента се въвежда:
```
http://localhost:8080/login/oauth2/code/google
```

### 2. `frontend/.env`

```properties
VITE_API_URL=http://localhost:8080
```

### 3. Инсталиране

От корена на проекта:
```bash
npm install
cd frontend && npm install
```

---

## Пускане

От корена на проекта, **с една команда**:
```bash
npm start
```

- Сайтът: **http://localhost:5173**
- API: **http://localhost:8080/api**

Спира се с `Ctrl+C`.

При първото пускане **Flyway** създава таблиците и зарежда игрите, жанровете и платформите автоматично.

<details>
<summary>Поотделно, в два терминала</summary>

```bash
cd backend && ./mvnw spring-boot:run
cd frontend && npm run dev
```
</details>

---

## База данни

8 таблици, създадени от Flyway миграции:

| Таблица | Съдържание |
|---|---|
| `users` | потребители (LOCAL или GOOGLE), роля, BCrypt hash на паролата, брояч на грешни опити |
| `games` | игрите (добавят се само чрез seed миграции) |
| `genres`, `platforms` | жанрове и платформи |
| `game_genres`, `game_platforms` | връзки много-към-много |
| `user_games` | колекцията: потребител + игра + платформа + тип (OWNED / WISHLIST) |
| `reviews` | ревюта: оценка 1–10 и текст, едно на игра от потребител |

Hibernate е с `ddl-auto: validate`: таблиците ги прави само Flyway, а Hibernate проверява дали entity-тата съвпадат с тях.

---

## API

### Вход и потребители

| Метод | Адрес | Достъп | Описание |
|---|---|---|---|
| POST | `/api/auth/register` | всички | регистрация → 201 + JWT |
| POST | `/api/auth/login` | всички | вход → 200 + JWT |
| GET | `/oauth2/authorization/google` | всички | вход с Google (от браузъра) |
| GET | `/api/users/me` | логнат | текущият потребител |
| GET | `/api/admin/users` | ADMIN | всички потребители |

### Каталог (публичен)

| Метод | Адрес | Описание |
|---|---|---|
| GET | `/api/games?search=&platformId=&genreId=&page=&size=` | игри с търсене, филтри и страници |
| GET | `/api/games/{id}` | детайли за игра |
| GET | `/api/genres` | жанрове |
| GET | `/api/platforms` | платформи |

### Колекция (логнат)

| Метод | Адрес | Тяло |
|---|---|---|
| GET | `/api/users/me/games?type=OWNED` | – (`type` е по желание) |
| POST | `/api/users/me/games` | `{ "gameId": 4, "platformId": 6, "type": "WISHLIST" }` |
| PATCH | `/api/users/me/games/{id}` | `{ "type": "OWNED" }` |
| DELETE | `/api/users/me/games/{id}` | – |

### Ревюта

| Метод | Адрес | Достъп |
|---|---|---|
| GET | `/api/games/{gameId}/reviews?page=0&size=10` | всички |
| POST | `/api/games/{gameId}/reviews` | логнат |
| PUT | `/api/reviews/{id}` | само авторът |
| DELETE | `/api/reviews/{id}` | авторът или ADMIN |
| GET | `/api/users/me/reviews` | логнат |

Тяло за POST и PUT: `{ "rating": 9, "text": "Страхотна игра" }`

Защитените адреси изискват хедър:
```
Authorization: Bearer <token>
```

### Статус кодове

| Код | Кога |
|---|---|
| 200 / 201 / 204 | успех |
| 400 | невалидни данни (имейл, оценка извън 1–10, липсващо поле, счупен JSON) |
| 401 | липсващ, невалиден или изтекъл токен; грешна парола |
| 403 | чуждо ревю или запис; адрес само за ADMIN |
| 404 | несъществуваща игра или адрес |
| 409 | зает имейл или username; играта вече е в колекцията; вече има ревю |
| 423 | акаунтът е заключен след 5 грешни опита (за 15 минути) |

---

## Сигурност

- **Пароли:** пазят се само като **BCrypt hash** (със salt), никога като текст.
- **JWT:** подписан с `JWT_SECRET` (HS256), валиден 24 часа. Сървърът е **stateless** и не пази сесии.
- **Валидация:** синтаксис на имейла, дължина и съдържание на паролата и username, оценка 1–10.
- **Заключване:** 5 грешни опита за вход → акаунтът се заключва за 15 минути.
- **Права:** роли USER и ADMIN; проверка за собственик при редакция и изтриване (403).
- **CORS:** разрешен само `http://localhost:5173`.
- **Вход с Google:** при същия имейл като съществуващ LOCAL акаунт Google входът влиза в същия акаунт.
- **Тайните** (`.env`) не са в Git.

---

## Тестване с Postman

Колекцията **Game Collection** е подредена в папки: **Auth**, **Каталог**, **Колекция**, **Ревюта**, **Грешки**.

- Environment с променлива `baseUrl = http://localhost:8080`.
- Заявката **Login** записва токена автоматично в `{{token}}` (Scripts → Post-response):
  ```js
  pm.environment.set("token", pm.response.json().token);
  ```
- Колекцията е с **Bearer Token `{{token}}`**, така че всички заявки го наследяват.
- Токенът изтича след 24 часа: преди демо се пуска пак **Login**.