# AGENTS.md - Keychain Project Guidance

## Project Overview
**Keychain** is a Spring Boot 4.0.6 application (Java 17) that manages device-related operations via HTTP communication. It's organized around a domain-driven structure with planned feature modules in empty directories.

## Architecture & Major Components

### Core Structure
- **Entry Point**: `KeychainApplication.java` - Standard @SpringBootApplication with no overrides
- **Layer Organization**: Follows Spring Boot conventions:
  - `client/` - HTTP communication layer (HttpClient component)
  - `model/` - Domain entities (Device is placeholder)
  - `controller/` - REST endpoints (empty placeholders)
  - `repository/` - Data access layer (empty placeholders)
  - `exception/` - Custom exceptions (empty)
  - `helpers/` - Utility functions (empty)
  - `validators/` - Input validation logic (empty)
  - `util/` - Constants and shared utilities (ChainConstants)

### HTTP Client Pattern
**Key File**: `HttpClient.java` (@Component)
- Single `send()` method handles GET/POST/PUT operations
- Supports both HTTP and HTTPS connections
- Returns `JsonObject` (GSON) for all responses
- 60-second connection timeout hardcoded
- Expects 200/201 HTTP status codes; throws RuntimeException otherwise
- **Pattern**: Direct use of HttpURLConnection with manual stream management
- **Note**: Has TODO comment about timeout optimization - consider refactoring when scaling

### Constants Management
**Key File**: `ChainConstants.java` (@UtilityClass via Lombok)
- Centralized HTTP header and method constants
- Use Lombok's @UtilityClass instead of private constructors
- Add new constants here for HTTP operations (Accept, Content-Type, Authorization headers)

## Build & Development Workflows

### Gradle Build System
```bash
# Compile
./gradlew build

# Run application
./gradlew bootRun

# Run tests
./gradlew test

# Build JAR (creates plain + executable JARs in build/libs/)
./gradlew bootJar
```
**Config File**: `build.gradle`
- Java 17 toolchain configured (not runtime version - auto-applies to all tasks)
- Spring Boot plugin auto-configures main class discovery
- Key Dependencies:
  - spring-boot-starter-web (REST support)
  - spring-boot-starter (core)
  - gson:2.14.0 (JSON serialization)
  - commons-io:2.18.0 (Stream utilities)
  - lombok:1.18.46 (Code generation - requires annotationProcessor)

### Testing Setup
- JUnit 5 (Jupiter) via spring-boot-starter-test
- Basic integration test exists: `KeychainApplicationTests.java` with @SpringBootTest
- **Pattern**: Use @SpringBootTest for component integration tests, @WebMvcTest for controller isolation
- No unit test examples yet in codebase

## Project-Specific Conventions

### Naming & Package Organization
- `com.drive.keychain.*` - all packages follow this root
- Domain packages grouped by technical layer (not business domain)
- Static utility classes use `@UtilityClass` (Lombok) annotation

### Logging
- **Logger**: SLF4J via `LoggerFactory.getLogger(ClassName.class)`
- Currently only used in HttpClient for error responses
- **Pattern**: Use synchronized log messages (not string interpolation in production)

### JSON Handling
- **Library**: GSON (Google JSON)
- **Pattern**: Use `JsonParser.parseString(String)` for parsing, `.getAsJsonObject()` for type conversion
- HttpClient returns `JsonObject` for all HTTP responses

### Configuration
- **File**: `application.properties` (Spring Boot standard)
- Currently minimal - `spring.application.name=keychain`
- Can extend with device registry endpoints, authentication keys, timeout values

## Integration Points

### HTTP Communication Flow
1. Controller (incoming REST request) → 2. Service/Helper method → 3. HttpClient.send() → 4. Returns JsonObject
2. **Exception Handling**: RuntimeException thrown for non-200/201 status codes (no custom exception hierarchy yet)
3. **Security**: Authorization header support in HttpClient; currently opt-in via method parameter

### Planned Service Boundaries (empty directories)
- **Controllers**: REST endpoints for device management
- **Repository**: Data persistence layer (potentially integrating with device registry or database)
- **Validators**: Input validation for device operations
- **Exceptions**: Custom exception types (DeviceNotFoundException, InvalidDeviceException, etc.)

## Development Tips for AI Agents

### Before Adding Features
1. Check if similar pattern exists in HttpClient (for HTTP operations) or ChainConstants (for constants)
2. Device model is currently empty - define fields/annotations before creating controllers
3. Consider Repository pattern for data access rather than direct database calls in controllers

### Common Tasks
- **New REST Endpoint**: Create in controller/ folder; @RestController, @GetMapping/@PostMapping, inject HttpClient or future service layer
- **New HTTP Operation**: Extend HttpClient.send() method signature or create new specific method; update ChainConstants for new header keys
- **New Model**: Add to model/ folder with Lombok annotations (@Data, @Builder) for auto-generation of getters/setters/equals/toString
- **Validation**: Use validators/ package; pattern similar to Spring validators or custom annotation processors

### Code Generation (Lombok)
- @Data - generates getters, setters, equals, hashCode, toString, constructor
- @Builder - fluent builder pattern for complex objects
- @Slf4j - auto-injects logger (use instead of manual LoggerFactory)
- @UtilityClass - replaces private constructor + static methods pattern

### Dependencies Already Available
- Spring Framework 7.0.7 (context support)
- Apache Commons I/O 2.18.0 (I/O utilities)
- No database dependencies yet planned
- No REST client library (using raw HttpURLConnection)

## Critical Known Issues & TODOs
1. **HttpClient.send()**: 60-second timeout is hardcoded; needs parameterization for different scenarios
2. **Error Handling**: Generic RuntimeException instead of custom exception hierarchy
3. **Resource Leaks**: HttpURLConnection not explicitly closed in all paths (consider try-with-resources)
4. **GET Method**: Stub implementation in switch statement
5. **Device Model**: Empty class - needs entity definition before CRUD operations possible

