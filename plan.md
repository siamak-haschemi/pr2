# Plan: Quiz & Flashcard Trainer (Programming 2 with Java)

## Motivation & Goals
- Provide a coherent, real-world Java project that progressively applies course topics: exceptions, testing, collections/sorting, (de-)serialization, and JavaFX GUI.
- Reinforce professional practices: Gradle builds, GitHub flow with PRs, CI, code formatting, static analysis, and packaging installers.
- Emphasize abstraction and configurability via a pluggable storage interface, connecting backend logic to a JavaFX UI through MVC.

## Scope & Choices (Recap)
- Features: Quizzes + flashcards; single correct answer per question (Anki-like simplified schema).
- UI: JavaFX with FXML + controllers; MVC; Scene Builder required.
- Java/Build: Java 21 (LTS); Gradle Kotlin DSL; no Java modules (no module-info.java).
- Testing: JUnit 5; JaCoCo report only (no threshold gate, for discussion).
- Formatting/Analysis: Spotless (Google Java Format); SpotBugs report-only.
- Persistence: Single “database-like” file in `~/.quiz-trainer` via pluggable storage (`NATIVE`, `JSON`, `XML`).
- Packaging: Badass Runtime Plugin + jpackage; unsigned installers for Windows + macOS (Apple Silicon).
- CI/CD: GitHub Actions matrix: `macos-14` and `windows-latest` builds; build/test/format/spotbugs/jacoco; package installers; GitHub Releases on tags `vM<n>`.
- Git workflow: Trunk-based; branches `milestone/<n>-<topic>`; instructor collaborator; protected `main`.

## Timeline & Milestones (Start 16.10.2025; every other week)
- M1 due: 30 Oct 2025
- M2 due: 13 Nov 2025
- M3 due: 27 Nov 2025
- M4 due: 11 Dec 2025
- Holidays (no deadlines): 22 Dec 2025 – 31 Dec 2025
- M5 due: 08 Jan 2026
- Buffers (within 16-week term): one flexible buffer before holidays; one after holidays.

## Milestone Details & Acceptance Criteria

### Milestone 1 — Project Bootstrap & Git Workflow
Goal: Students start from a fully functioning, instructor-provided JavaFX app. Establish Git/GitHub workflow and CI visibility.
- Tasks
  - Create a GitHub account (if needed) and a private team repository.
  - Enable branch protection on `main`; add the instructor as collaborator and PR reviewer.
  - Create a feature branch (e.g., `milestone/1-bootstrap`).
  - Commit and push the provided Milestone 1 code (fully functioning app with build, tests, formatting, SpotBugs, and packaging preconfigured).
  - Open a Pull Request and add the instructor as reviewer.
  - Run the provided GitHub Action manually to build/test and package; verify artifacts:
    - macOS: zipped `.app` bundle only
    - Windows: `.exe` installer only
- Acceptance
  - PR created with correct branch naming and instructor added as reviewer.
  - CI workflow run is green and expected artifacts are attached to the Release.
  - Student can demonstrate local run via Gradle and basic PR review etiquette.

### Milestone 2 — Exceptions, Testing, and Quiz UI
Goal: Implement validated core model with custom exceptions and unit tests, plus a simple quiz UI built from feature-specific views/controllers.
- Tasks
  - Model and exceptions
    - `Question` (text, options, single correct index) and `Quiz` (title + questions)
    - Validation: non-empty text, at least one option, correct index within bounds; non-empty quiz title
    - Custom exception: `InvalidQuestionException`
    - JUnit 5 tests for valid/invalid construction and behaviors
  - Fixed quiz fixture
    - Add `QuizFixtures.fixedJUnitAndExceptionsQuiz()` with 10 curated questions
  - JavaFX UI by feature
    - `main.MainController` + `main/main.fxml` (StackPane host): switches views
    - `overview.OverviewController` + `overview/overview.fxml`: show quiz title and list of questions, mark correct answers; “Start Quiz” button
    - `quiz.QuizExecutionController` + `quiz/quiz_execution.fxml`: radio-button answers, Continue (feedback), Next (advance), return to overview when finished
- Acceptance
  - Tests pass in CI; Spotless/SpotBugs run; coverage is visible (not gating)
  - Manual run shows overview with 10 questions; Start Quiz → execution flow with immediate feedback; “Next” cycles through and returns to overview

### Milestone 3 — Collections and Sorting
Goal: Manage multiple quizzes/decks; sorting and searching.
- Tasks
  - `QuizManager`: add/remove/list quizzes; search/filter by title/tag; statistics (question count).
  - Sorting using `Comparable`/`Comparator` by title, created date, and number of questions.
  - JUnit tests covering sort order and searches.
  - Basic JavaFX list view showing sorted quizzes (observable list binding).
- Acceptance
  - Tests cover sorting and searching.
  - Demo displays a sorted list in a simple JavaFX view.

### Milestone 4 — (De-)Serialization and Configurability
Goal: Pluggable storage with JSON, XML, and native; runtime selectable via settings.
- Tasks
  - Define `StorageService` interface and `StorageFormat { NATIVE, JSON, XML }`.
  - Implement `JsonStorageService` (Jackson), `XmlStorageService` (JAXB), `NativeStorageService` (Java serialization, with safety notes in docs).
  - Config service reads/writes `~/.quiz-trainer/config.json` with fields: `storageFormat`, `dataFile`.
  - Load quizzes on startup; save on exit; handle I/O errors with domain exceptions.
  - Round-trip tests (save→load→equals) for each format; handle backward-compatible schema evolution field defaults.
- Acceptance
  - In-app Settings toggles storage format; config file updated; app reflects selection on restart.
  - Round-trip tests pass for all formats; CI green.

### Milestone 5 — Full JavaFX GUI
Goal: End-to-end UI connected to backend with basic validation and score view.
- Tasks
  - Views: quiz/deck selection; create/edit quizzes and questions; play quiz (single correct answer); score screen.
  - Controllers: wire up events; input validation; user feedback (alerts/labels).
  - Data binding: observable lists for quizzes/questions; form field bindings for editing.
  - Packaging: CI builds installers on tag `vM5` and creates a GitHub Release.
- Acceptance
  - Demo: create → save → reload → play → score; error handling visible for invalid input.
  - Release `vM5` provides installers for macOS (Apple Silicon) and Windows.

## Serialization Architecture
- Abstraction
  - `StorageService`: `save(AllData data)`, `AllData load()`, `Path getDataPath()`, `void setDataPath(Path)`; throws `StorageException`.
  - `StorageFormat`: `NATIVE`, `JSON`, `XML`.
  - `ConfigService`: loads/saves `~/.quiz-trainer/config.json` with `{ storageFormat, dataFile }`.
- Simplified Anki-like Schema (JSON)
  - Top level
    ```json
    {
      "format": "anki-lite",
      "version": 1,
      "decks": [
        {
          "name": "Default",
          "cards": [
            { "front": "Q1?", "back": "A1", "tags": ["tag1"], "due": "2025-10-30" }
          ]
        }
      ]
    }
    ```
  - XML mirrors JSON structure with `<decks>`, `<deck>`, `<cards>`, `<card>` elements.
  - Native Java serialization stores the same `AllData` object graph.

## CI/CD Pipeline (GitHub Actions)
- Triggers: PRs to `main`, and tags `vM<n>`.
- Jobs (matrix): `macos-14`, `windows-latest`.
- Steps: checkout → setup JDK 21 → cache Gradle → `./gradlew spotlessCheck test jacocoTestReport spotbugsMain spotbugsTest` → package via Badass/jpackage → upload artifacts.
- On tag: create GitHub Release and attach installers.
- Reports: JaCoCo and SpotBugs published as artifacts or PR comments (report-only for SpotBugs).

## Packaging
- Plugin: `org.beryx.runtime` (Badass Runtime Plugin) to build custom runtime image and jpackage installers.
- Outputs (unsigned):
  - Windows: MSI + portable app image
  - macOS (Apple Silicon): DMG + portable app image
- No code signing; installers are for classroom/testing via CI artifacts and Releases.

## Git Workflow & Repository Rules
- Branching: trunk-based; feature/milestone branches named `milestone/<n>-<topic>`.
- PRs: one PR per milestone; instructor is collaborator and reviewer; simple PR description.
- Protection: `main` requires PRs and passing CI checks; no direct pushes.
- Tags: `vM1`..`vM5` create Releases with installers.

## Template Repo Structure (Scaffold)
```
.
├─ .github/
│  └─ workflows/
│     └─ ci.yml                      # CI matrix: macOS-14 + Windows; build, test, package
├─ .gitattributes
├─ .gitignore
├─ .editorconfig
├─ README.md                         # Course readme (provided)
├─ START-HERE.md                     # 1-page student checklist
├─ settings.gradle.kts
├─ build.gradle.kts
├─ gradle/
│  └─ wrapper/                       # Gradle wrapper files
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ com/example/quiztrainer/
│  │  │     ├─ App.java              # JavaFX entry point
│  │  │     ├─ MainController.java   # Controller for main.fxml
│  │  │     ├─ controller/
│  │  │     │  ├─ QuizListController.java
│  │  │     │  ├─ EditorController.java
│  │  │     │  ├─ PlayController.java
│  │  │     │  └─ ScoreController.java
│  │  │     ├─ model/
│  │  │     │  ├─ Question.java
│  │  │     │  ├─ Quiz.java
│  │  │     │  ├─ QuizManager.java
│  │  │     │  └─ exception/
│  │  │     │     ├─ InvalidQuestionException.java
│  │  │     │     └─ StorageException.java
│  │  │     ├─ storage/
│  │  │     │  ├─ StorageService.java
│  │  │     │  ├─ StorageFormat.java
│  │  │     │  ├─ JsonStorageService.java
│  │  │     │  ├─ XmlStorageService.java
│  │  │     │  └─ NativeStorageService.java
│  │  │     ├─ config/
│  │  │     │  ├─ AppConfig.java
│  │  │     │  └─ ConfigService.java
│  │  │     └─ util/
│  │  │        └─ DateUtils.java
│  │  └─ resources/
│  │     ├─ main.fxml
│  │     ├─ quiz_list.fxml
│  │     ├─ quiz_editor.fxml
│  │     ├─ play_quiz.fxml
│  │     ├─ score.fxml
│  │     └─ application.css
│  └─ test/
│     └─ java/
│        └─ com/example/quiztrainer/
│           ├─ model/
│           │  ├─ QuestionTest.java
│           │  └─ QuizManagerTest.java
│           └─ storage/
│              └─ StorageRoundTripTest.java
├─ scripts/
│  ├─ local-run.sh                   # Optional local helpers
│  └─ package.sh
└─ docs/
   ├─ architecture.md
   └─ serialization-schema.md
```

## Student Onboarding (START-HERE.md Outline)
- Install [JDK 21](https://www.oracle.com/de/java/technologies/downloads/), [IntelliJ IDEA](https://www.jetbrains.com/idea/download), and [JavaFX Scene Builder](https://gluonhq.com/products/scene-builder/#download).
- Clone repo; run `./gradlew build` then run app.
- Configure branch protection; add instructor as collaborator.
- Create `milestone/1-hello-javafx` branch; push; open PR; ensure CI passes and artifacts appear.

## Presentation & Grading
- Each milestone: live demo + PR review + Q&A. Any student may be asked about any part.
- Pass/fail per milestone; all five required to qualify for the final exam.

## Risks & Mitigations
- Packaging on student machines: use CI artifacts and Releases.
- Scene Builder/version drift: pin JavaFX in Gradle; provide setup tips.
- Serialization complexity: simplified schema; comprehensive round-trip tests.
- Coverage pressure: report-only JaCoCo; use numbers for discussion, not gates.
