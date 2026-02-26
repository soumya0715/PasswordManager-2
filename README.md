
# RevPasswordManager

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 19.2.21.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
=======
# Password Manager - Backend (Java/Spring Boot)

Professional Password Manager backend built with Java, Spring Boot, and MySQL.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **MySQL 8.0**
- **Spring Security + JWT**
- **Maven**
- **JUnit 5**
- **Mockito**

## Project Structure

```
backend/
├── src/main/java/com/passwordmanager/
│   ├── entity/              # JPA Entities
│   ├── repository/          # Spring Data Repositories
│   ├── service/             # Business Logic
│   ├── controller/          # REST Controllers
│   ├── dto/                 # Data Transfer Objects
│   ├── security/            # JWT & Security Config
│   └── PasswordManagerApplication.java
├── src/main/resources/
│   └── application.yml      # Configuration
├── src/test/java/          # JUnit Tests
└── pom.xml                 # Maven Dependencies
```

## Features

✅ User Authentication (Register, Login with JWT)
✅ Password CRUD Operations
✅ Categories Management
✅ Favorites Management
✅ Search Functionality
✅ Password Strength Calculation
✅ Security Questions
✅ Two-Factor Authentication Ready
✅ Full JUnit Test Coverage
✅ CORS Enabled

## Installation

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### Setup Steps

1. **Clone/Extract the project**
```bash
cd backend
```

2. **Create MySQL Database**
```sql
CREATE DATABASE password_manager_db;
```

3. **Configure Database**
Edit `src/main/resources/application.yml`:
```yaml
datasource:
  url: jdbc:mysql://localhost:3306/password_manager_db
  username: root
  password: your_password
```

4. **Configure JWT Secret**
Edit `application.yml`:
```yaml
jwt:
  secret: your_secret_key_here_minimum_32_characters_long
  expiration: 86400000
```

5. **Build the Project**
```bash
mvn clean install
```

6. **Run the Application**
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080/api`

## Running Tests

```bash
mvn test
```

Run specific test:
```bash
mvn test -Dtest=AuthServiceTest
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Passwords
- `GET /api/passwords` - Get all passwords
- `GET /api/passwords/{id}` - Get single password
- `POST /api/passwords` - Create password
- `PUT /api/passwords/{id}` - Update password
- `DELETE /api/passwords/{id}` - Delete password
- `POST /api/passwords/{id}/favorite` - Toggle favorite
- `GET /api/passwords/favorites` - Get favorite passwords
- `GET /api/passwords/search?keyword=` - Search passwords

## Database Schema

### Users Table
- id (PK)
- email (unique)
- password (hashed)
- firstName
- lastName
- twoFAEnabled
- emailVerified
- createdAt
- updatedAt

### Passwords Table
- id (PK)
- title
- username
- password (encrypted)
- email
- url
- notes
- strengthScore
- isFavorite
- userId (FK)
- categoryId (FK)
- createdAt
- updatedAt

### Categories Table
- id (PK)
- name
- description
- userId (FK)

### Security Questions Table
- id (PK)
- question
- answer
- userId (FK)

## Security Features

1. **Password Encryption**: Passwords are hashed using BCrypt
2. **JWT Authentication**: Stateless token-based auth
3. **CORS**: Configured for frontend domain
4. **SQL Injection Prevention**: Uses JPA parameterized queries
5. **Request Validation**: Jakarta Validation annotations
6. **Security Headers**: Implemented in SecurityConfig

## Build & Deployment

### Maven Build
```bash
mvn clean package -DskipTests
```

### JAR Execution
```bash
java -jar target/password-manager-backend-1.0.0.jar
```

### Environment Variables
```bash
SERVER_PORT=8080
DB_URL=jdbc:mysql://localhost:3306/password_manager_db
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_secret_key_here
```

## Troubleshooting

**Port Already in Use:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

**Database Connection Error:**
- Verify MySQL is running
- Check credentials in application.yml
- Ensure database exists

**JWT Token Invalid:**
- Verify JWT secret is configured
- Check token format: "Bearer <token>"
- Verify token hasn't expired

## Frontend Integration

Frontend should:
1. Send login request to `/api/auth/login`
2. Store JWT token from response
3. Include token in header: `Authorization: Bearer <token>`
4. API calls to protected endpoints require JWT token

## License

MIT License

## Contact

For issues or questions, contact : Soumya20901@gmail.com.

