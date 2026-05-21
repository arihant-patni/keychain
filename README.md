# Keychain

Keychain is a small Spring Boot (Spring Framework 7.x / Spring Boot 4.x compatible) Java application (Java 17) that demonstrates a lightweight, domain-oriented structure for automating TODO apis and todo-related operations via HTTP and JDBC.

This README explains the project structure, key components, the libraries used and why they were chosen, what you can accomplish with the project, and how the controller/service/repository layers fit together.

---

# Objective

To have a multimodule framework for backend automation where we can have rest apis automated and can connect with mysql database. Also can be exted to verify and connect to other data sources.
 - In this framework we have framework, which can be part of another repo in future and can be used as jar
 - Test suite
 - Rest controllers - With help of this if helpers need to be created as service for manual testing or data creation etc use cases.

## Project overview

- Language: Java 17
- Framework: Spring (Spring Boot style conventions)
- Build: Gradle (wrapper included)
- JSON handling: GSON
- Purpose: Provide HTTP client and JDBC examples in a layered architecture. The project includes an HTTP client utility (`HttpClient`), domain models (e.g., `Todo`, `Device`), repository examples using JDBC, and test helpers.

This project is organized by technical layers rather than feature modules so it's straightforward to extend with new business features.

---

## Key components and where to find them

- `src/main/java/com/drive/keychain/KeychainApplication.java`
  - Spring Boot application entry point.

- `client/HttpClient.java`
  - A small @Component HTTP client using `HttpURLConnection` to perform GET/POST/PUT requests and returning `com.google.gson.JsonObject` results.
  - Designed for simplicity and to avoid adding a heavy external HTTP client dependency. Useful for simple HTTP integrations.

- `util/ChainConstants.java`
  - Centralized constant definitions for HTTP headers and methods. Uses Lombok's `@UtilityClass` to expose static constants without a manual private constructor.

- `model/` (e.g., `Todo`, `Device`)
  - Domain entities. The pattern uses Lombok annotations (e.g., `@Data`, `@Builder`) for concise models.

- `repository/` (e.g., `JdbcTodoRepository`)
  - JDBC-based data access layer using `JdbcTemplate` or a thin `JdbcClient` wrapper. Keeps SQL and mapping logic inside repository implementations.

- `helpers/` (e.g., `TodoDbHelper`)
  - Small helpers that orchestrate repository usage. Helpful in tests or in transitional code when services are not yet introduced.

- `config/` (e.g., `ExternalApiConfig`, `DataSourceConfig`)
  - Configuration classes for external API endpoints and data source wiring.

---

## Libraries and why they were chosen

- Spring Boot / Spring Web
  - Provides dependency injection, configuration, and REST support with minimal boilerplate.

- GSON (com.google.code.gson:gson)
  - Lightweight JSON library used in `HttpClient` for parsing and returning `JsonObject` results. Chosen for simplicity and small footprint.

- Lombok
  - Reduces boilerplate for models and utility classes (`@Data`, `@Builder`, `@UtilityClass`, etc.). Speeds up development of POJOs and builders.

- Apache Commons IO
  - Small helpers for stream handling (used in `HttpClient` for safe stream copying/reading).

- Spring Boot Test (JUnit Jupiter)
  - Test support for Spring context tests and writing unit/integration tests.

Notes: The project intentionally uses Java's built-in `HttpURLConnection` and Spring's JDBC support rather than including a heavy HTTP client library or ORM - this keeps dependencies small and the examples educational.

---

## What you can achieve with this project

- Learn or prototype HTTP integrations using a small, self-contained `HttpClient`.
- Develop JDBC-based data access patterns with `JdbcTemplate` or a thin `JdbcClient` wrapper.
- Build REST controllers that call services/helpers and repositories in a layered architecture.
- Add domain logic (devices, todos) and integrate with external APIs via `HttpClient`.

Typical extension points:
- Add a service layer (e.g., `service/` package) to contain business logic, invoked by controllers.
- Add custom exceptions and validators to improve error handling and input validation.
- Add a repository implementation that connects to a real database (MySQL, Postgres) by providing `db-config.properties` or ENV variables.

---

## Why controllers? (role of controllers in this framework)

Controllers (`@RestController`) are the entry points for external HTTP requests. In this project they:

- Map incoming HTTP requests to handler methods (GET/POST/PUT/DELETE).
- Validate and normalize input (or delegate to validators).
- Delegate to services or helpers which coordinate repository access and external API calls.
- Convert domain objects to HTTP responses (DTOs or JSON) and set appropriate status codes.

Keeping controllers thin (delegating to services/helpers) helps keep business logic testable and separate from transport concerns.

---

## Configuration and database

- `src/main/resources/application.properties` contains Spring Boot basic configuration.
- `src/main/resources/db-config.properties` includes JDBC connection settings used by repository integration tests or by a `DataSourceConfig`.

---

## How to build and run

From the project root (wrapper scripts included):

Windows PowerShell examples:

```
.\gradlew clen build -x test
.\gradlew bootRun
```

---

## Tests

- Unit tests use JUnit 5 (Jupiter). Some tests mock dependencies to remain fast and deterministic.
- Integration-style tests may connect to a configured database. This project includes test helpers to centralize DataSource/DriverManager creation if you prefer real DB checks.

Best practices:
- Keep unit tests fast by mocking external services or repositories.
- Keep integration tests separate (or gated) so CI can run them only when a database is available.

---

## Contributing & coding conventions

- Follow standard Spring layering: controllers → services/helpers → repositories.
- Use Lombok for models and builders (`@Builder`, `@Data`) to keep classes concise.
- Centralize constants (like HTTP header names) in `util/ChainConstants.java`.

---

## Known TODOs and improvements

- Refactor `HttpClient.send()` to make timeouts configurable and use try-with-resources consistently to avoid resource leaks.
- Replace raw `HttpURLConnection` with a higher-level HTTP client (e.g., `HttpClient` from Java 11+ or Apache HttpClient) if you need advanced features.
- Add a service layer and move orchestration out of helpers for clearer separation of concerns.
- Add a robust exception hierarchy instead of throwing plain `RuntimeException` on HTTP or DB errors.

---

## Contact / Further reading

This README is a starting point. Explore the `src/main/java` and `src/test/java` folders to see code examples for HTTP calls, JDBC usage, and test helpers.

If you want, a follow-up README section can be added that documents common developer tasks (adding a controller, new repository method, or how to run integration tests against your DB).

