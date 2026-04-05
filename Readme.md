## Finance Dashboard Backend (Assignment)

This project provides a clean, role-aware backend for a finance dashboard. It supports:

- User and role management (Viewer, Analyst, Admin)
- Financial record CRUD with filtering
- Dashboard summary and trend analytics
- JWT-based authentication
- Validation and consistent error responses

### Tech Stack

- Spring Boot (Web, Security, Validation)
- Spring Data JPA
- PostgreSQL (default)
- JWT for stateless auth

### Roles & Access Control

- `VIEWER`: Can view dashboard summary + trends only
- `ANALYST`: Can view dashboard + read financial records
- `ADMIN`: Full access (users + records + dashboard)

Enforcement is done at the HTTP security layer and reinforced in services.

### Data Model

**User**
- `id`, `name`, `email`, `password`, `role`, `status`
- `status`: `ACTIVE`, `INACTIVE`

**FinancialRecord**
- `id`, `createdBy`, `type`, `category`, `amount`, `date`, `notes`
- `type`: `INCOME`, `EXPENSE`

### API Endpoints

**Auth**
- `POST /auth/register` — Register (first user becomes `ADMIN`)
- `POST /auth/login` — Obtain JWT

**Users (Admin only, except /me)**
- `GET /users` — List users
- `GET /users/{id}` — User by ID
- `GET /users/me` — Current user
- `POST /users` — Create user with role/status
- `PATCH /users/{id}` — Update name/role/status/password
- `DELETE /users/{id}` — Soft deactivate (sets `INACTIVE`)

**Records**
- `POST /records` — Create record (Admin)
- `GET /records` — List records with optional filters
- `GET /records/{id}` — Record by ID
- `PATCH /records/{id}` — Update record (Admin)
- `DELETE /records/{id}` — Delete record (Admin)

Filters for `GET /records`:
- `type`, `category`, `createdBy`, `start`, `end`

**Dashboard**
- `GET /dashboard/summary` — Totals, category totals, recent activity
- `GET /dashboard/trends` — Monthly trend points

Optional params:
- `summary`: `start`, `end`, `createdBy`
- `trends`: `months` (default 6), `createdBy`

### Validation & Errors

- Input validation via `jakarta.validation`
- Consistent error payloads:
```
{
  "message": "Validation failed",
  "details": { "field": "reason" },
  "timestamp": "2026-04-04T12:00:00Z",
  "path": "/records"
}
```

### Running Locally

1. Update `demo/src/main/resources/application.properties` with your DB config.
2. Start PostgreSQL and create a DB + user that matches your config.
3. Run the app:
```
./mvnw spring-boot:run
```

Default port: `8800`

### Example Requests

Register:
```bash
curl -X POST http://localhost:8800/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Ava","email":"ava@fin.com","password":"password123"}'
```

Login:
```bash
curl -X POST http://localhost:8800/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ava@fin.com","password":"password123"}'
```

Create a record (Admin):
```bash
curl -X POST http://localhost:8800/records \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"type":"INCOME","category":"Salary","amount":2500,"date":"2026-04-01","notes":"April salary"}'
```

### Notes & Assumptions

- `/auth/register` creates a `VIEWER` user, except the very first user which becomes `ADMIN`.
- The first registered user is promoted to `ADMIN` to bootstrap the system.
- Admins can set roles/status via `/users`.
- Passwords are hashed using BCrypt.
- This is an assessment-oriented backend, optimized for clarity and maintainability.
