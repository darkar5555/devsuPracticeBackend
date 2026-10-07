# devsu-practice-backend

Spring Boot, Java 21 REST API. Endpoints are served under `/api`.

```bash
./mvnw spring-boot:run   # needs PostgreSQL with the schema of ../BaseDatos.sql so first up Docker
./mvnw test
```

Configuration by environment variables (defaults for local development): `POSTGRES_HOST`,
`POSTGRES_PORT`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `SERVER_PORT`,
`BANK_DAILY_WITHDRAWAL_LIMIT` (1000).

```
com.devsu.bank
  domain          entities, enums, business exceptions
  application     services (use cases), repository ports, report model
  infrastructure  JPA adapters, controllers, DTOs, error handler, PDF, configuration
```
