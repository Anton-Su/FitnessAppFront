# Тестирование REST API

## 1. Регистрация пользователя

### POST /auth/register
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Ответ:**
```json
{
  "user_id":1,
  "username":"testuser",
  "message":"User registered successfully",
  "token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInVpZCI6MSwiZXhwIjoxNzc5MjgyODUyLCJpYXQiOjE3NzkyNzkyNTIsInVzZXJuYW1lIjoidGVzdHVzZXIifQ.BLFTi9kyDAp4L3uAih3dn_LFEHwUvdhbwMksWlb3oIQ",
  "tokenType":"Bearer",
  "expiresIn":3600
}
```

---

## 2. Вход пользователя

### POST /auth/login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Ответ:**
```json
{
  "user_id": 1,
  "username": "testuser",
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInVpZCI6MSwiZXhwIjoxNzc5MjgyODUyLCJpYXQiOjE3NzkyNzkyNTIsInVzZXJuYW1lIjoidGVzdHVzZXIifQ.BLFTi9kyDAp4L3uAih3dn_LFEHwUvdhbwMksWlb3oIQ",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

## 3. Получить все активности

### GET /activities
```bash
curl http://localhost:8080/activities \
  -H "Authorization: Bearer <token>"
```

**Ответ:**
```json
[
  {
    "activity_id": 1,
    "user": {
      "user_id": 1,
      "username": "testuser",
      "email": "test@example.com"
    },
    "activity_date": "2026-04-24T10:30:00.000+00:00",
    "steps": 5000,
    "burnt": 250,
    "goal_achieved": true
  }
]
```

---

## 4. Создать активность

### POST /activities
```bash
curl -X POST http://localhost:8080/activities \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "user": {
      "user_id": 1
    },
    "activity_date": "2026-04-24T10:30:00Z",
    "steps": 5000,
    "burnt": 250,
    "goal_achieved": true
  }'
```

**Ответ:**
```json
{
  "activity_id": 1,
  "user": {
    "user_id": 1,
    "username": "testuser"
  },
  "activity_date": "2026-04-24T10:30:00.000+00:00",
  "steps": 5000,
  "burnt": 250,
  "goal_achieved": true
}
```

---

## 5. Получить активность по ID

### GET /activities/{id}
```bash
curl http://localhost:8080/activities/1 \
  -H "Authorization: Bearer <token>"
```

---

## 6. Получить активности пользователя

### GET /activities/user/{userId}
```bash
curl http://localhost:8080/activities/user/1 \
  -H "Authorization: Bearer <token>"
```

---

## 7. Обновить активность

### PUT /activities/{id}
```bash
curl -X PUT http://localhost:8080/activities/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "activity_date": "2026-04-24T10:30:00Z",
    "steps": 6000,
    "burnt": 300,
    "goal_achieved": false
  }'
```

---

## 8. Удалить активность

### DELETE /activities/{id}
```bash
curl -X DELETE http://localhost:8080/activities/1 \
  -H "Authorization: Bearer <token>"
```

**Ответ:**
```json
{
  "message": "Activity deleted successfully"
}
```

---

## 9. Работа со справочником упражнений

### GET /exercises
```bash
curl http://localhost:8080/exercises \
  -H "Authorization: Bearer <token>"
```

**Ответ:**
```json
[
  {
    "exercise_id": 1,
    "name": "Push-up",
    "description": "Классические отжимания от пола",
    "file_path": "/images/exercises/push-up.png"
  }
]
```

### GET /exercises/{id}
```bash
curl http://localhost:8080/exercises/1 \
  -H "Authorization: Bearer <token>"
```

### POST /exercises
```bash
curl -X POST http://localhost:8080/exercises \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "Push-up",
    "description": "Классические отжимания от пола",
    "file_path": "/images/exercises/push-up.png"
  }'
```

### PUT /exercises/{id}
```bash
curl -X PUT http://localhost:8080/exercises/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "Push-up",
    "description": "Обновленное описание упражнения",
    "file_path": "/images/exercises/push-up-v2.png"
  }'
```

### DELETE /exercises/{id}
```bash
curl -X DELETE http://localhost:8080/exercises/1 \
  -H "Authorization: Bearer <token>"
```

---

## Структура проекта

```
src/main/java/github/m1xexsu/stdfitnessappserver/
├── controller/
│   ├── AuthController.java       # Регистрация и вход
│   ├── ActivityController.java   # CRUD операции для активности
│   └── ExercisesController.java  # CRUD операции для упражнений
├── service/
│   ├── UserService.java          # Бизнес-логика пользователя
│   ├── ActivityService.java      # Бизнес-логика активности
│   └── ExercisesService.java     # Бизнес-логика упражнений
├── repository/
│   ├── UserRepository.java       # Доступ к БД (User)
│   ├── ActivityRepository.java   # Доступ к БД (Activity)
│   ├── ExercisesRepository.java   # Доступ к БД (Exercises)
│   └── ProfileRepository.java    # Доступ к БД (Profile)
├── entity/
│   ├── UserEntity.java           # Таблица User
│   ├── ActivityEntity.java       # Таблица Activity
│   ├── ExercisesEntity.java      # Таблица Exercises
│   └── ProfileEntity.java        # Таблица Profile
├── dto/
│   ├── RegisterRequest.java      # DTO для регистрации
│   ├── LoginRequest.java         # DTO для входа
│   └── LoginResponse.java        # DTO для ответа входа
└── config/
    └── SecurityConfig.java       # Spring Security конфигурация
```

---

## Как запустить

1. **Убедитесь, что MariaDB запущена**
   - Хост: localhost:3306
   - БД: stdfitness
   - Пользователь: root
   - Пароль: your_password (обновите в application.properties)

2. **Запустите приложение**
   ```bash
   ./gradlew bootRun
   ```

3. **Приложение будет доступно на** `http://localhost:8080`

---

## Для Postman

Импортируйте эту коллекцию (сохраните как `postman_collection.json`):

```json
{
  "info": {
    "name": "STD Fitness API",
    "version": "1.0.0"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Register",
          "request": {
            "method": "POST",
            "header": [{"key": "Content-Type", "value": "application/json"}],
            "body": {
              "mode": "raw",
              "raw": "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\"}"
            },
            "url": {"raw": "http://localhost:8080/auth/register"}
          }
        },
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "header": [{"key": "Content-Type", "value": "application/json"}],
            "body": {
              "mode": "raw",
              "raw": "{\"username\":\"testuser\",\"password\":\"password123\"}"
            },
            "url": {"raw": "http://localhost:8080/auth/login"}
          }
        }
      ]
    },
    {
      "name": "Activities",
      "item": [
        {
          "name": "Get All",
          "request": {
            "method": "GET",
            "header": [{"key": "Authorization", "value": "Bearer <token>"}],
            "url": {"raw": "http://localhost:8080/activities"}
          }
        },
        {
          "name": "Create",
          "request": {
            "method": "POST",
            "header": [
              {"key": "Content-Type", "value": "application/json"},
              {"key": "Authorization", "value": "Bearer <token>"}
            ],
            "body": {
              "mode": "raw",
              "raw": "{\"user\":{\"user_id\":1},\"activity_date\":\"2026-04-24T10:30:00Z\",\"steps\":5000,\"burnt\":250,\"goal_achieved\":true}"
            },
            "url": {"raw": "http://localhost:8080/activities"}
          }
        }
      ]
    },
    {
      "name": "Exercises",
      "item": [
        {
          "name": "Get All",
          "request": {
            "method": "GET",
            "header": [{"key": "Authorization", "value": "Bearer <token>"}],
            "url": {"raw": "http://localhost:8080/exercises"}
          }
        },
        {
          "name": "Create",
          "request": {
            "method": "POST",
            "header": [
              {"key": "Content-Type", "value": "application/json"},
              {"key": "Authorization", "value": "Bearer <token>"}
            ],
            "body": {
              "mode": "raw",
              "raw": "{\"name\":\"Push-up\",\"description\":\"Классические отжимания от пола\",\"file_path\":\"/images/exercises/push-up.png\"}"
            },
            "url": {"raw": "http://localhost:8080/exercises"}
          }
        }
      ]
    }
  ]
}
```

---

## Note

- **Авторизация**: API использует JWT Bearer токены. Пароли хранятся в виде BCrypt-хэша.
  Для production обычно дополнительно настраивают:
  - refresh token
  - отзыв токенов (logout / revoke)
  - роли и права доступа
  
- **CSRF**: Отключен для разработки. Для production нужно включить.

- **CORS**: Разрешены все источники для разработки.

