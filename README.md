# ratatouille-backend

Ktor-based backend service written in Kotlin. It exposes a simple HTTP API (currently a root endpoint) and connects to a PostgreSQL database using HikariCP and Exposed. The project was bootstrapped with the Ktor Project Generator and uses the Gradle Kotlin DSL.

## Stack

- Language: Kotlin (kotlin_version = 2.2.20)
- Runtime/Server: Ktor 3.3.1 on Netty
- Build tool / package manager: Gradle (Wrapper, Kotlin DSL)
- Java toolchain: JDK 21
- Database: PostgreSQL
- DB access: Exposed ORM + HikariCP connection pool
- Logging: Logback + Ktor CallLogging
- Auth (planned): Firebase Admin + Ktor Firebase auth provider (currently not enabled in code)

## Entry points

- Main class: `io.ktor.server.netty.EngineMain` (configured via Gradle)
- Ktor module: `com.sukakotlin.ApplicationKt.module` (configured in `src/main/resources/application.conf`)
- Server port: `8080` (see `application.conf`)

## Features (current)

- Routing: GET `/` returns "Hello World!" (see `src/main/kotlin/Routing.kt`)
- Static content served from classpath at `/static` (see `src/main/resources/static/`)
- Request logging via `CallLogging` (see `src/main/kotlin/Monitoring.kt`)
- Database init + simple migration system on startup (see `core/database`)

## Requirements

- JDK 21 (Gradle will use the configured toolchain)
- Gradle wrapper (bundled): `./gradlew`
- PostgreSQL server (local or remote)

Optional / planned:
- Firebase Admin credentials (for auth) — not wired yet (code is commented out)

## Configuration

Configuration is managed via `src/main/resources/application.conf` and environment variables.

- `application.conf` excerpt:
  - `ktor.application.modules = [ com.sukakotlin.ApplicationKt.module ]`
  - `ktor.deployment.port = 8080`
  - `ktor.database.*` reads from environment variables below

Environment variables (required for DB):
- `DB_URL` e.g. `jdbc:postgresql://localhost:5432/ratatouille_db`
- `DB_USER` database user name
- `DB_PASSWORD` database user password

Notes:
- A sample `.env` file exists at project root. Ktor does not load `.env` files by default; export variables in your shell or configure a dotenv loader if desired. TODO: Add dotenv support or document a preferred approach.

## Setup

1. Ensure PostgreSQL is running and accessible.
   - Create a database, e.g. `ratatouille_db`.
   - Create a user with privileges or reuse an existing one.
2. Export required environment variables in your shell (example for bash/zsh):
   ```bash
   export DB_URL="jdbc:postgresql://localhost:5432/ratatouille_db"
   export DB_USER="youruser"
   export DB_PASSWORD="yourpassword"
   ```
3. Build the project:
   ```bash
   ./gradlew build
   ```
4. Run the server:
   ```bash
   ./gradlew run
   ```
   The server listens on `http://0.0.0.0:8080`.

## Common Gradle tasks

- `./gradlew run` — run the server in development
- `./gradlew test` — run tests
- `./gradlew build` — compile and package
- `./gradlew buildFatJar` — create an executable fat JAR (provided by Ktor Gradle plugin)
- `./gradlew buildImage` — build a Docker image for the fat JAR (plugin-provided)
- `./gradlew publishImageToLocalRegistry` — publish Docker image to local registry (plugin-provided)
- `./gradlew runDocker` — run the app using the locally built image

Caveats:
- Docker-related tasks are provided by the Ktor Gradle plugin; registry and image settings are not customized in this repo. TODO: Document image name, tags, and any required registry setup, or provide a `Dockerfile` if preferred.

## Database and migrations

On startup, `DatabaseFactory.init()` reads DB config, initializes a Hikari pool, and connects via Exposed. It then runs custom migrations defined in `core/database`:
- `SchemaVersion` table tracks the current migration version.
- New migrations can be added to the `migrations` list in `Migrations.kt` and will be applied in order when the app starts.

## API quick check

- GET `http://localhost:8080/` → 200 OK with "Hello World!"
- Static file: `http://localhost:8080/static/index.html`

## Tests

- Run: `./gradlew test`
- Example test: `src/test/kotlin/ApplicationTest.kt` starts the Ktor test application and asserts the root path responds with 200 OK.

## Project structure

```
.
├── build.gradle.kts                # Gradle build (Kotlin DSL)
├── gradle.properties               # Versions (Kotlin, Ktor, Logback)
├── gradle/                         # Wrapper and version catalog
├── settings.gradle.kts
├── src
│   ├── main
│   │   ├── kotlin
│   │   │   ├── Application.kt      # Entry, installs DB init
│   │   │   ├── Monitoring.kt       # CallLogging
│   │   │   ├── Routing.kt          # Routes (/, static)
│   │   │   ├── Security.kt         # Authentication (Firebase stub, commented)
│   │   │   └── core/               # DB, utilities, features
│   │   └── resources
│   │       ├── application.conf    # Ktor + DB config
│   │       ├── logback.xml         # Logging config
│   │       └── static/             # Public static assets
│   └── test
│       └── kotlin
│           └── ApplicationTest.kt  # Simple smoke test
├── .env                            # Sample DB env vars (not auto-loaded)
└── README.md
```

## Environment and operations

- Port: `8080` (configure in `application.conf`)
- Logs: configured via Logback; HTTP request logging via `CallLogging`
- Metrics: Micrometer + Prometheus registry dependency is present but not installed/configured in code. TODO: install and expose `/metrics` or similar if needed.
- Authentication: Firebase Admin dependencies are present, but `configureSecurity()` is commented out. TODO: provide service account JSON and enable the provider.

## License

No license file is present in this repository. TODO: Add a LICENSE file (e.g., MIT, Apache-2.0) and update this section accordingly.

## Useful links

- Ktor Documentation: https://ktor.io/docs/home.html
- Ktor GitHub: https://github.com/ktorio/ktor
- Kotlin Slack (Ktor channel available): https://surveys.jetbrains.com/s3/kotlin-slack-sign-up

