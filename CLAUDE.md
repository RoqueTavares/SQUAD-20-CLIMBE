# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build the project
./gradlew clean bootJar

# Run locally (requires PostgreSQL running)
./gradlew bootRun

# Run tests (uses H2 in-memory DB automatically)
./gradlew test

# Run a single test class
./gradlew test --tests "com.squad20.sistema_climbe.api.ClassName"

# Start only the database (for local dev)
docker-compose up -d db

# Full Docker deployment
docker-compose up
```

## Environment Setup

Copy `application.properties.example` to `application.properties` and configure:
- PostgreSQL connection (default: `localhost:5433`)
- JWT secret and expiration (access: 30 min, refresh: 7 days)
- Google OAuth2 client credentials
- CORS allowed origins

Tests use `application-test.properties` with H2 — no manual setup needed.

## Architecture

**Spring Boot 4.0.3 + Java 17 REST API** using a domain-driven layered architecture.

### Package Structure

```
com.squad20.sistema_climbe/
├── config/          # Security, CORS, OAuth2, OpenAPI, JWT filter
├── controller/      # AuthenticationController (login, register, refresh, logout)
├── domain/          # 13 domain modules (each self-contained)
├── dto/             # Global DTOs (AuthRequest, TokenResponse)
├── exception/       # GlobalExceptionHandler + custom exceptions
└── service/         # AuthenticationService
```

### Domain Modules

Each of the 13 domain modules under `domain/` follows the same vertical slice pattern:
```
domain/<name>/
├── controller/   # REST endpoints
├── service/      # Business logic
├── entity/       # JPA entities
├── dto/          # Request/response DTOs
├── mapper/       # MapStruct entity↔DTO mappers
└── repository/   # Spring Data JPA repositories
```

Domains: `user`, `cargo`, `contract`, `document`, `enterprise`, `meeting`, `notification`, `permission`, `proposal`, `report`, `service`, `spreadsheet`, `security` (refresh tokens).

### Authentication Flow

1. POST `/api/auth/register` or `/api/auth/login` → returns JWT access token (30 min) + refresh token (7 days) as HttpOnly cookies
2. `JwtAuthenticationFilter` validates JWT on every request
3. POST `/api/auth/refresh` exchanges refresh token cookie for a new access token
4. OAuth2 Google login handled by `CustomOAuth2SuccessHandler` → issues same JWT cookies

### Key Config Classes

- `SecurityConfig` — JWT filter chain, OAuth2, public vs. protected routes
- `ApplicationConfig` — BCrypt password encoder, `AuthenticationManager` bean
- `CorsConfig` — CORS policy (origins from `application.properties`)
- `GlobalExceptionHandler` — Converts `ResourceNotFoundException`, `ConflictException`, `BadRequestException`, `TokenRefreshException` to HTTP responses

### API Documentation

Swagger UI available at runtime: `http://localhost:8080/swagger-ui.html`
OpenAPI spec: `http://localhost:8080/v3/api-docs`
