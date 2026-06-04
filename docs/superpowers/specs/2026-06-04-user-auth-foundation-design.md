# CareerBridge User Auth Foundation Design

## Scope

This first MVP slice builds the backend foundation for user signup, login, role handling, common API responses, global exception handling, and JWT-based authentication.

Included:

- User entity and repository
- User role and status enums
- Signup API
- Login API
- BCrypt password hashing
- JWT access token issuing and validation
- Spring Security configuration
- Common API response wrapper
- Global exception handler
- Focused tests for signup, login, and duplicate email behavior

Excluded for this slice:

- Refresh tokens
- Logout
- Email verification
- Password reset
- OAuth/social login
- Mentor and mentee profile registration
- Admin user management screens or APIs

These excluded features are valuable, but they add new flows and storage concerns. The first slice should establish a reliable authentication base before expanding.

## Goals

The implementation should let a user register, log in, and receive a JWT access token that future APIs can use to identify the caller.

The API should follow the project response shape:

```json
{
  "success": true,
  "message": "Request succeeded.",
  "data": {}
}
```

Errors should follow the same shape with `success: false` and `data: null`.

The design should be understandable for a developer learning backend architecture. Responsibilities should stay separated so each layer has one clear job.

## Architecture

The feature will use a conventional Spring layered architecture:

- `user.controller`: HTTP request and response handling
- `user.service`: signup and login business logic
- `user.repository`: database access through Spring Data JPA
- `user.entity`: JPA entity and enums
- `user.dto`: request and response DTOs
- `global.security`: JWT, authentication filter, and security configuration
- `global.response`: common response model
- `global.exception`: shared exception handling and domain exception base types

This separation keeps controllers thin. Controllers should not hash passwords, query repositories directly, or create JWTs. Those are service/security responsibilities.

## Data Model

`users` table:

- `id BIGINT PK`
- `email VARCHAR UNIQUE NOT NULL`
- `password VARCHAR NOT NULL`
- `name VARCHAR NOT NULL`
- `role VARCHAR NOT NULL`
- `status VARCHAR NOT NULL`
- `created_at DATETIME NOT NULL`
- `updated_at DATETIME`

Enums:

- `UserRole`: `MENTEE`, `MENTOR`, `ADMIN`
- `UserStatus`: `ACTIVE`, `INACTIVE`

The entity will extend a shared `BaseTimeEntity` so created and updated timestamps are consistent across future domains.

Passwords will never be stored as plain text. The service will encode passwords with `PasswordEncoder`, backed by BCrypt.

## API Design

### Signup

`POST /api/users/signup`

Request:

```json
{
  "email": "mentee@example.com",
  "password": "password123!",
  "name": "Kim Mentee",
  "role": "MENTEE"
}
```

Success response:

```json
{
  "success": true,
  "message": "Signup succeeded.",
  "data": {
    "id": 1,
    "email": "mentee@example.com",
    "name": "Kim Mentee",
    "role": "MENTEE",
    "status": "ACTIVE"
  }
}
```

Duplicate email should return an error response and an appropriate HTTP status.

### Login

`POST /api/auth/login`

Request:

```json
{
  "email": "mentee@example.com",
  "password": "password123!"
}
```

Success response:

```json
{
  "success": true,
  "message": "Login succeeded.",
  "data": {
    "accessToken": "...",
    "tokenType": "Bearer"
  }
}
```

Invalid credentials should not reveal whether the email or password was wrong.

## Security Flow

Signup and login are public endpoints.

Future protected endpoints will require:

```text
Authorization: Bearer <access-token>
```

The JWT filter will:

1. Read the bearer token from the request header.
2. Validate token signature and expiration.
3. Extract user identity and role claims.
4. Put an authenticated principal into Spring Security's context.

This design makes future authorization checks simple because controllers and services can rely on Spring Security to identify the current user.

## Error Handling

The project will use `@RestControllerAdvice` for global error handling.

Initial exception types:

- `DuplicateEmailException`
- `InvalidLoginException`
- `UserNotFoundException`
- `UnauthorizedAccessException`

Validation errors from request DTOs should also be converted into the common response format.

This keeps error responses predictable for frontend clients.

## Performance and Scalability Considerations

For the first slice, the most important performance decision is the unique index on `users.email`. It prevents duplicate data and makes login lookup efficient.

BCrypt is intentionally slower than ordinary hashing. This is good for password security, but the cost factor should remain at Spring's sensible default for MVP. If login throughput later becomes a bottleneck, we can tune the cost factor with measurements.

JWT access tokens avoid a database lookup for every request when only basic identity and role data are needed. However, this also means token revocation is not instant without additional storage. That is why refresh tokens and logout are deferred until we introduce a token lifecycle policy.

The design keeps Redis out of the first slice. Redis becomes useful when refresh tokens, login session tracking, rate limiting, or token deny-lists are added.

## Testing Strategy

Tests should cover:

- Successful signup
- Duplicate email rejection
- Password stored as a BCrypt hash, not plain text
- Successful login returns a bearer access token
- Invalid login returns a safe error response

The first implementation can use service-level tests for business rules and lightweight controller tests for request/response format. Full security integration tests can be added once protected domain APIs exist.

## Teaching Notes

This feature is a good place to learn three backend principles:

1. Layering: controllers handle HTTP, services handle business rules, repositories handle persistence.
2. Boundary safety: DTOs prevent exposing entity internals directly through the API.
3. Security responsibility split: password hashing belongs in signup/login logic, while token validation belongs in the security layer.

As implementation proceeds, explanations should call out why each class exists, what it depends on, and what future feature would use it.
