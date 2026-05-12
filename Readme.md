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

### Environment Variables
- `VITE_API_URL` — base URL for the backend (default `http://localhost:8080/codex-example/api/v1`)
- `VITE_INACTIVITY_MINUTES` — auto-logout inactivity minutes (default 30)
- `VITE_QUIET_HOURS` — suppress medication alerts during hours, format `HH-HH` (e.g., `22-07`)
- `VITE_VISIT_REMIND_MINUTES` — comma-separated minutes before a visit to alert (default `60,1440`)

### Implemented Features
- Contacts: Add/list patient-linked contacts; one-tap Call links.
- Medications: Add/list/archive meds with schedule; PRN logging requires reason; offline log queue with auto-sync.
- Reminders: Local UI reminders for scheduled meds with acknowledge/snooze/mark-given; quiet hours support and user preferences.
- Visits: Add upcoming visits, reminders at configurable offsets, complete with notes/vitals/care changes; view past visits.
- Symptoms: Record timestamped symptom entries with tags, notes, optional vitals, and pain score; filter by date or tag.
- Secure Photos: Capture/upload photos (camera/gallery) with consent gate; images are encrypted at rest and served via auth-only endpoint; linkable to symptoms and med logs.
- Daily Checklist: Configurable care tasks with frequency; mark complete; offline completion queue and history view.
- Emergency Plan: Quick-access screen with hotline/nurse 1-tap call, patient context, optional location (not stored), and links to guidance.
- Education Library: Simple searchable topics with per-user completed tracking.
- Exports: CSV summary download for a date range (photos excluded by default).
- Care Team (MVP): Invite/accept flows with roles (Owner/Caregiver/Viewer); audit-logged. Data sharing wiring to roles is a future enhancement.
- Notification Preferences: Configure channels and quiet hours; test notification action.

## Auth Overview
- Register: `POST /codex-example/api/v1/auth/register` (email + password)
- Login: `POST /codex-example/api/v1/auth/login` (JWT returned)
- Me: `GET /codex-example/api/v1/auth/me` (requires `Authorization: Bearer <token>`)
- Forgot Password: `POST /codex-example/api/v1/auth/forgot-password` (generates reset token)
- Reset Password: `POST /codex-example/api/v1/auth/reset-password` (use token + new password)

In production, configure HTTPS termination to ensure transport encryption.

## Security & Privacy
- All API endpoints protected by JWT except auth/register/login/reset and invite acceptance.
- Photo uploads are encrypted at rest (AES-GCM) and only served to authenticated users; URLs contain no PHI.
- Minimal PHI in notifications; exports exclude photos by default.
- Audit log captures key actions (med CRUD, symptoms, visits, checklist completions, login/logout, invites).

## Seed Demo User
- Backend seeds a demo account on startup: `demo@example.com` / `Password123!`
- The login screen displays these credentials for quick exploration.

## Git & CI
- Use `mvn` (not `./mvnw`) for Maven commands.
- Conventional commits encouraged: `feat:`, `fix:`, `chore:`, `test:`

## Contributing
- Follow layered architecture: Controller -> Service -> Repository
- Use DTOs for request/response; do not expose entities
- Validate inputs with Jakarta Bean Validation
- Use Lombok annotations for boilerplate
- Use conventional commits
