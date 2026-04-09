# Project Title
Courier Management (Auth & Role Management Module)

## Overview
This is a Spring Boot backend project currently focused on authentication and role-based user management for a courier system.  
At this stage, the project implements secure login/register flow with JWT and protected admin endpoints for user role creation.

## Current Project Status
⚠️ This project is **in progress** and currently includes only the auth/security and user-role management module.  
Courier shipment/order/tracking features are not implemented yet.

## Features Implemented
- User entity with role support (`ADMIN`, `MANAGER`, `DELIVERY_AGENT`, `CUSTOMER`).
- Customer self-registration endpoint.
- Login endpoint with JWT token generation.
- Password hashing using `BCryptPasswordEncoder`.
- JWT validation filter for secured API access.
- Spring Security configuration with stateless-style JWT filter flow.
- Role-protected admin endpoints:
  - create manager
  - create delivery agent
- Method-level authorization using `@PreAuthorize`.

## Tech Stack
- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- MySQL
- Lombok
- Maven

## System Design / How It Works
1. A customer registers via `/auth/register`.
2. User password is encrypted using BCrypt before storing in DB.
3. User logs in via `/auth/login` and receives JWT token.
4. For protected APIs, client sends:
   - `Authorization: Bearer <token>`
5. `JwtFilter` validates token, extracts email + role, and sets Spring Security context.
6. Admin endpoints under `/admin/**` are accessible only when token has role `ADMIN`.

## Project Structure
```text
src/main/java/com/example/CourierManagement
├── CourierManagementApplication.java
├── config
│   ├── AppConfig.java
│   ├── SecurityConfig.java
│   ├── JwtFilter.java
│   └── JwtUtil.java
├── controller
│   ├── AuthController.java
│   └── AdminController.java
├── service
│   └── AuthService.java
├── entity
│   └── User.java
├── repository
│   └── UserRepository.java
├── dto
│   ├── RegisterRequest.java
│   └── LoginRequest.java
└── enums
    └── UserRole.java
```

## Setup & Installation
1. Install Java 17, Maven, and MySQL.
2. Create database:
   - `courier_db`
3. Update `src/main/resources/application.properties`:
   - `spring.datasource.url`
   - `spring.datasource.username`
   - `spring.datasource.password`
4. Build and run:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## API Endpoints (Implemented)

### Public Endpoints
- `POST /auth/register`  
  Register customer (role set as `CUSTOMER` internally).

- `POST /auth/login`  
  Login and receive JWT token.

### Protected Endpoints (ADMIN only)
- `POST /admin/create-manager`  
  Create user with `MANAGER` role.

- `POST /admin/create-agent`  
  Create user with `DELIVERY_AGENT` role.

## Database Schema (Current)

### `user`
- `id` (PK)
- `name`
- `email` (unique, not null)
- `password` (encrypted, not null)
- `role` (enum string)

## Configuration Notes
- `/auth/**` is publicly accessible.
- All other endpoints require authenticated JWT token.
- Method security is enabled with `@EnableMethodSecurity`.
- Current JWT secret is hardcoded in `JwtUtil` (development style).
- Hibernate setting: `spring.jpa.hibernate.ddl-auto=update`.

## Not Yet Implemented (Planned Scope)
- Courier booking APIs
- Parcel assignment to delivery agent
- Shipment status updates (picked, in-transit, delivered)
- Tracking endpoint
- Manager/agent operational workflows
- Global exception handling and validation layer
- Test coverage

## Future Improvements
- Move JWT secret and expiry to environment variables.
- Add refresh token flow.
- Add DTO validations (`@Valid`) and centralized error handling.
- Add courier domain entities (shipment, parcel, status history).
- Add unit/integration tests.
- Add Swagger/OpenAPI documentation.
