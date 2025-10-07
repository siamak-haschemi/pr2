# Milestones (Reference Snapshots)

This folder contains five self-contained, minimal Gradle projects — one per milestone — so students can inspect and compare versions over time. Each subfolder builds independently and includes:

- Gradle build files (`build.gradle.kts`, `settings.gradle.kts`)
- Minimal Java sources and resources aligned to the milestone
- JUnit tests where applicable

Note: To keep this repository lightweight, Gradle Wrapper JAR is not committed. The wrapper scripts and properties are also not included in these subprojects. Students can use a system Gradle or copy the top-level wrapper (if present) to build locally, or build via CI.

## Projects
- `m1-hello-javafx`: Hello world JavaFX + Gradle + basic CI/packaging intent
- `m2-exceptions-junit`: Core model + validation + custom exceptions + JUnit
- `m3-collections-sorting`: `QuizManager` + sorting/searching + tests
- `m4-serialization`: Pluggable storage interface and stubs (JSON/XML/Native) + config
- `m5-gui-final`: Multi-view JavaFX skeleton wired to backend stubs

Each project is intentionally minimal but complete enough to run or extend during the course.
