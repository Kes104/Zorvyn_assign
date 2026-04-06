## Finance Dashboard Backend (Assignment)

This is a backend project for a Finance Dashboard built using Spring Boot.

This project supports the following features:

- User and role management (Viewer, Analyst, Admin)
- Financial record management by an Analyst or an Admin
- Dashboard summary and trend analytics
- JWT-based authentication
- Validation and consistent error responses

### Tech Stack

- Spring Boot (Web, Security, Validation)
- Spring Data JPA
- PostgreSQL (default)
- JWT for stateless auth

### Roles & Access Control

- `VIEWER`: Can only view dashboard summary and trends
- `ANALYST`: Can view dashboard and read financial records
- `ADMIN`: Complete access to number of users and the financial records along with summary dashboard.

Role-based access is handled using Spring Security configuration and also checked inside the service layer where required.

### Data Model

The application mainly uses two entities: User and FinancialRecord.

**User**
- `id`, `name`, `email`, `password`, `role`, `status`
- `status`: `ACTIVE`, `INACTIVE`

**FinancialRecord**
- `id`, `createdBy`, `type`, `category`, `amount`, `date`, `notes`
- `type`: `INCOME`, `EXPENSE`

### API Endpoints

Below are the main API endpoints available in the system :

**Auth**
- `POST /auth/register` — Register (optional role; first user becomes `ADMIN` if role omitted)
- `POST /auth/login` — Obtain JWT

**Users (Admin only, except /me)**
- `GET /users` — List users
- `GET /users/{id}` — User by ID
- `GET /users/me` — Current user
- `POST /users` — Create user with role/status (status defaults to `ACTIVE`)
- `PATCH /users/{id}` — Update name/role/status/password
- `PATCH /users/{id}/toggle-status` — Toggle `ACTIVE`/`INACTIVE`
- `DELETE /users/{id}` — Delete user (hard delete)

**Records**
- `POST /records` — Create record (Admin)
- `GET /records` — List records
- `GET /records/{id}` — Record by ID
- `PATCH /records/{id}` — Update record (Admin)
- `DELETE /records/{id}` — Delete record (Admin)

**Dashboard**
- `GET /dashboard/summary` — Totals, category totals, recent activity (auth required)
- `GET /dashboard/trends` — Monthly trend points (auth required, default 6 months)

### Validation & Errors

- Input validation via `jakarta.validation`
- The application returns errors in a common JSON format like this:
```
{
  "message": "Validation failed",
  "details": { "field": "reason" },
  "timestamp": "2026-04-04T12:00:00Z",
  "path": "/records"
}
```

### Running Locally

Follow these steps to run it locally and verify yourself

1. Update `demo/src/main/resources/application.properties` with your DB config.
2. Start PostgreSQL and create a DB + user that matches your config.
3. Run the app:
```
./mvnw spring-boot:run
```
Default port: `8800`
4. Go to [Swagger docs](http://localhost:8800/swagger-ui/index.html#/) and try for yourself.

### Example Requests

Register:
```bash
curl -X POST http://localhost:8800/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Ava","email":"ava@fin.com","password":"password123","role":"ANALYST"}'
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
- You can optionally pass `role` (`ADMIN`, `ANALYST`, `VIEWER`) during registration.
- The first registered user is promoted to `ADMIN` only if `role` is omitted.
- Admins can set roles/status via `/users`.
- Passwords are hashed using BCrypt.
- This project was built mainly to demonstrate backend design, security, and database     modeling using Spring Boot.
