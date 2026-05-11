# Wiggens Hospice Assist App

Monorepo containing a Spring Boot backend (`api/`) and a React + Vite TypeScript frontend (`ui/`).

## Tech Stack
- Backend: Spring Boot 3, Spring Data JPA, Spring Security, H2 (dev) / PostgreSQL (prod), Maven
- Frontend: React + Vite + TypeScript, MUI, Axios
- Testing: JUnit 5 + Spring Boot Test (backend), Vitest + React Testing Library (frontend)

## Project Structure
```
ralph-timesheet/
  api/          # Spring Boot backend (Maven project)
  ui/           # React frontend (Vite + TypeScript)
  Agents.md     # Project instructions
  PROMPT.md     # Issue loop prompt
```

## Backend

### Run
```
cd api && mvn spring-boot:run
```

### Test
```
cd api && mvn test
```

### Build
```
cd api && mvn clean package
```

The API listens on `http://localhost:8080` in development. REST endpoints are under `/codex-example/api/v1/`.

## Frontend

### Install
```
cd ui && npm install
```

### Run Dev Server
```
cd ui && npm run dev
```

### Test
```
cd ui && npm test
```

### Build
```
cd ui && npm run build
```

The dev server runs on `http://localhost:5173` by default and proxies API requests to `http://localhost:8080` when configured.

## Auth Overview
- Register: `POST /codex-example/api/v1/auth/register` (email + password)
- Login: `POST /codex-example/api/v1/auth/login` (JWT returned)
- Me: `GET /codex-example/api/v1/auth/me` (requires `Authorization: Bearer <token>`)
- Forgot Password: `POST /codex-example/api/v1/auth/forgot-password` (generates reset token)
- Reset Password: `POST /codex-example/api/v1/auth/reset-password` (use token + new password)

In production, configure HTTPS termination to ensure transport encryption.

## Contributing
- Follow layered architecture: Controller -> Service -> Repository
- Use DTOs for request/response; do not expose entities
- Validate inputs with Jakarta Bean Validation
- Use Lombok annotations for boilerplate
- Use conventional commits

