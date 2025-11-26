# System Rejestracji i Logowania

## Przegląd
System uwierzytelniania użytkowników oparty na JWT (JSON Web Token) z możliwością rejestracji i logowania.

## Struktura Projektu

### Controllers
- **AuthController** (`controller/AuthController.java`)
  - `POST /api/auth/register` - Rejestracja nowego użytkownika
  - `POST /api/auth/login` - Logowanie użytkownika

### Services
- **UserService** (`service/UserService.java`)
  - Zarządzanie użytkownikami
  - Implementacja UserDetailsService dla Spring Security
  - Walidacja unikalności username i email
  
- **AuthService** (`service/AuthService.java`)
  - Logika rejestracji
  - Logika logowania
  - Generowanie tokenów JWT

### Security
- **SecurityConfig** (`security/SecurityConfig.java`)
  - Konfiguracja Spring Security
  - Definicja PasswordEncoder (BCrypt)
  - Konfiguracja AuthenticationManager
  
- **JwtTokenProvider** (`security/JwtTokenProvider.java`)
  - Generowanie tokenów JWT
  - Walidacja tokenów
  - Wyciąganie danych z tokenów
  
- **JwtAuthenticationFilter** (`security/JwtAuthenticationFilter.java`)
  - Filtr uwierzytelniania na podstawie tokenów JWT
  - Automatyczne uwierzytelnianie użytkownika przy każdym żądaniu

### DTOs
- **RegisterRequest** - Dane do rejestracji (username, email, password)
- **LoginRequest** - Dane do logowania (usernameOrEmail, password)
- **AuthResponse** - Odpowiedź z danymi użytkownika i tokenem JWT

### Repository
- **UserRepository** (`repository/UserRepository.java`)
  - Interfejs JPA do operacji na bazie danych
  - Metody wyszukiwania po username i email

### Exception Handler
- **GlobalExceptionHandler** (`exception/GlobalExceptionHandler.java`)
  - Obsługa błędów walidacji
  - Obsługa wyjątków runtime

## Endpointy API

### Rejestracja
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "user123",
  "email": "user@example.com",
  "password": "password123"
}
```

**Odpowiedź (201 Created):**
```json
{
  "id": 1,
  "username": "user123",
  "email": "user@example.com",
  "balance": 0,
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer"
}
```

### Logowanie
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "user123",
  "password": "password123"
}
```

**Odpowiedź (200 OK):**
```json
{
  "id": 1,
  "username": "user123",
  "email": "user@example.com",
  "balance": 0,
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer"
}
```

## Użycie Tokena JWT

Po zalogowaniu, token należy dołączać do każdego chronionego żądania w nagłówku:

```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## Konfiguracja

W pliku `application.properties`:

```properties
# JWT Configuration
app.jwt.secret=mySecretKeyForJWTTokenGenerationMustBeLongEnoughForHS512Algorithm
app.jwt.expiration=86400000
```

- `app.jwt.secret` - Klucz tajny do podpisywania tokenów (zmień w produkcji!)
- `app.jwt.expiration` - Czas wygaśnięcia tokenu w milisekundach (24h)

## Walidacja

### RegisterRequest
- `username`: wymagane, 3-50 znaków
- `email`: wymagane, poprawny format email
- `password`: wymagane, minimum 6 znaków

### LoginRequest
- `usernameOrEmail`: wymagane
- `password`: wymagane

## Bezpieczeństwo

- Hasła są szyfrowane za pomocą BCrypt
- Tokeny JWT są podpisywane algorytmem HS512
- Sesje są bezstanowe (stateless)
- Endpointy `/api/auth/**` są publicznie dostępne
- Wszystkie inne endpointy wymagają uwierzytelnienia

## Uruchomienie

1. Pobierz zależności Maven:
```bash
./mvnw clean install
```

2. Uruchom aplikację:
```bash
./mvnw spring-boot:run
```

3. Aplikacja będzie dostępna pod adresem: `http://localhost:8080`

## Zależności

- Spring Boot 3.5.7
- Spring Security
- Spring Data JPA
- JWT (jjwt) 0.12.3
- Lombok
- PostgreSQL
- Validation
