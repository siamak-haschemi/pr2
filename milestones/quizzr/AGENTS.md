# Repository Guidelines

## Project Structure & Modules
- Source: `src/main/java` (module `de.bht.pr.quizzr.swing`, entry `de.bht.pr.quizzr.swing.SwingApp`).
- Tests: `src/test/java` (JUnit 5; name tests `*Test.java`).
- Build output: `build/` (includes `image/` and `jpackage/` artifacts).
- Gradle config: `build.gradle.kts`, `settings.gradle.kts`, wrapper in `gradlew*`, `gradle/`.

## Build, Test, Run
- `./gradlew clean build` — Compile, run tests, Spotless/SpotBugs checks.
- `./gradlew test` — Run unit tests (JUnit Platform).
- `./gradlew run` — Launch the Swing app from sources.
- `./gradlew jlink` — Create a custom runtime image under `build/image`.
- `./gradlew jpackage` — Create a desktop bundle (e.g., macOS `.app`).

## Coding Style & Naming
- Java 21 toolchain; format with Spotless (Google Java Format). Run `./gradlew spotlessApply`.
- Packages: lower-case dot paths (e.g., `de.bht.pr.quizzr.swing`).
- Classes: `PascalCase`; methods/fields: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Keep code modular; update `module-info.java` when adding exported packages.

## Testing Guidelines
- Framework: JUnit 5. Place tests in `src/test/java` mirroring package paths.
- Name tests `ClassNameTest.java`; prefer clear, independent tests.
- Run locally via `./gradlew test`; inspect reports under `build/reports/tests/test/`.

## Commit & PR Guidelines
- Commits: concise, imperative subject (≤72 chars), body for rationale when needed.
- Group related changes; avoid mixing refactors with features.
- PRs: include description, scope, screenshots/GIFs for UI changes, and linked issue/ID.
- CI expectations: PRs should pass `build`, `test`, Spotless, and SpotBugs.

## Security & Environment
- Require JDK 21; use the Gradle wrapper (`./gradlew`) to ensure consistent builds.
- Do not commit secrets or platform-specific artifacts in `build/`.
- For packaging on macOS/Windows, verify `jpackage` settings before distribution.

## Contributor Quickstart
```
./gradlew spotlessApply test run
```
