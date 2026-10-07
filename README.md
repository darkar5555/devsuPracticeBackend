# Devsu Bank

Technical test for Devsu

Backend with Java, Spring Boot, Spring Data JPA, PostgreSQL 17, OpenPDF. Folder devsuPracticeBackend.
Frontend with Angular 22, Jest, plain CSS. Folder devsuPracticeFrontend
Docker for create BBDD. Folder devsuPracticeDocker
Database script BaseDatos.sql
Postman devsu-bank-api.postman_collection.json.

## Run everything with Docker

Requires Docker Desktop (Compose v2).

```bash
cd devsuPracticeDocker
cp .env.example .env
docker compose up -d --build
```

The first start builds both images and loads BaseDatos.sql to get tables and data.

Web client | http://localhost:4200
API http://localhost:8080/api. Example http://localhost:8080/api/clientes
PostgreSQL localhost:5433 with .env.example, user devsu, database devsu_bank

## Run locally for development

1. Database: `cd devsuPracticeDocker && cp .env.example .env && docker compose up -d postgres`
2. Backend: `cd devsuPracticeBackend && ./mvnw spring-boot:run` (reads `devsuPracticeDocker/.env`
   for the connection, serves http://localhost:8080/api)
3. Frontend: `cd devsuPracticeFrontend && npm install && npm start` (http://localhost:4200, the
   dev server forwards `/api` to the backend)

## API

All endpoints live under `/api`.

| `/api/clientes` | `GET`, `POST`, `GET /{id}`, `PUT /{id}`, `PATCH /{id}`, `DELETE /{id}` |
| `/api/cuentas` | `GET`, `POST`, `GET /{id}`, `PUT /{id}`, `PATCH /{id}`, `DELETE /{id}` |
| `/api/movimientos` | `GET`, `POST`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |
| `/api/reportes?fecha=YYYY-MM-DD,YYYY-MM-DD&cliente={id}` | `GET`: account statement as JSON, with the PDF in `pdfBase64` 

### Postman

Import `devsu-bank-api.postman_collection.json`. The `baseUrl` variable defaults to
`http://localhost:8080/api`.

```bash
npx newman run devsu-bank-api.postman_collection.json
```

## Tests

```bash
cd devsuPracticeBackend && ./mvnw test
cd devsuPracticeFrontend && npm test
```

## I use hexagonal for backend and Angular for frontend with this features:

- **Backend layers**: `domain` (entities, business exceptions), `application` (use cases and
  repository ports), `infrastructure` (JPA adapters, REST controllers, DTO records, PDF
  rendering, configuration). Controllers only map DTOs; rules live in the services.
- **Data model**: `Person` is a `@MappedSuperclass`; `Customer` extends it and adds password and
  status. `Account` belongs to a customer; `Transaction` belongs to an account. Primary keys are
  `<table>_id`; enums are stored as text with check constraints. `BaseDatos.sql` is the source
  of truth and Hibernate runs with `ddl-auto=validate`.
- **Frontend**: standalone components, signals for state, reactive forms with the same validation
  rules as the API, lazy-loaded routes. Validation messages and server errors are shown on screen in Spanish. No CSS framework: a global stylesheet with CSS variables and a responsive layout (sidebar on desktop, top bar on small screens).
- **Single origin**: the client calls `/api` relatively; in development the Angular dev server
  proxies it and in Docker nginx does, so no CORS configuration is needed.
