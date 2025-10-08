Plan Overview

- MVVM with clear separation: models, persistence, services, viewmodels, views.
- JSON persistence (Jackson) at ~/.quizzr/quizzes.json, autosave 5s, no backups.
- Single instance: acquire lock at startup; if lock fails, show message and exit.
- Import/Export: export current collection; import aborts on any name conflict.
- UI: Top tabs (Home, Editor, Practice), dark theme, dialogs for question editing, validations inline.
- IntelliJ GUI Designer: .form files + GridLayoutManager; Gradle instruments forms headless.

Packages

- de.bht.pr.quizzr.swing.model — POJOs: QuizCollection, Quiz, Question, Answer, QuestionType.
- de.bht.pr.quizzr.swing.persistence — JsonRepository, FileLockManager, PathsProvider.
- de.bht.pr.quizzr.swing.service — AutosaveService, ImportExportService, ValidationService.
- de.bht.pr.quizzr.swing.viewmodel — HomeViewModel, EditorViewModel, PracticeViewModel, SummaryViewModel.
- de.bht.pr.quizzr.swing.view — MainFrame and panels (Home, Editor, Practice), modal dialogs (QuestionDialog.form).
- de.bht.pr.quizzr.swing.app — AppBootstrap, theme setup, navigation wiring.
- de.bht.pr.quizzr.swing.util — Debouncer, Either/Result types, small helpers.

Data Model

- QuizCollection { List<Quiz> quizzes }
- Quiz { UUID id, String name, List<Question> questions }
- Question { UUID id, String text, QuestionType type, List<Answer> answers, Set<UUID> correctAnswerIds }
- Answer { UUID id, String text }
- QuestionType { SINGLE, MULTIPLE }
- Rules: quiz names unique (case-insensitive); min two answers; SINGLE requires exactly one correct; MULTIPLE requires ≥1 correct.

Persistence & Single Instance

- Storage: ~/.quizzr/quizzes.json (create folder/file if missing).
- Load on startup; on JSON parse error: show error and offer “Start fresh” (empties collection in memory; then save).
- Autosave: 5s debounce on any mutation; manual “Save Now”.
- Lock: lock file under ~/.quizzr/quizzr.lock via FileChannel.tryLock(). If lock fails, show dialog and exit (no read-only mode).
- No backups and no restore UI.

Import/Export

- Export: write entire collection to a user-chosen JSON file.
- Import: read file; validate there are no quiz name collisions (case-insensitive). If any conflict, throw and show error; do not mutate current data.

MVVM Contracts (essentials)

- HomeViewModel
    - State: list of quizzes (read-only view), search query.
    - Commands: create, rename, duplicate, delete (with confirm), select quiz, saveNow.
- EditorViewModel
    - State: selected quiz, questions list with order.
    - Commands: add/remove/reorder questions; open/edit question (dialog); apply validated changes.
- PracticeViewModel
    - State: settings (order original/shuffle, subset count), current index, feedback state.
    - Commands: start, submit, next/prev, finish; provides summary.
- SummaryViewModel
    - State: score, per-question results; allows quick retry of incorrect (in-memory).
- Observability: PropertyChangeSupport; all UI updates on EDT; persistence on background executor.

UI (Swing + FlatLaf Dracula)

- MainFrame with tabs: Home, Editor, Practice; menu/toolbar: Save Now, Import, Export.
- Home: list of quizzes (name, question count), search box, create/rename/duplicate/delete (confirm).
- Editor: quiz name field; questions list with reorder; “Edit Question” dialog (.form) with:
    - Question text; type selector (single/multiple).
    - Answers table/list with add/edit/remove/reorder, checkboxes for correct choices.
    - Inline validation; disable OK/Apply when invalid.
- Practice: settings (order, subset), one-question-at-a-time, immediate feedback on submit, progress, final summary and review.
- Keyboard: New quiz (Ctrl/Cmd+N), Delete (Del), Start practice (Ctrl/Cmd+R), Save (Ctrl/Cmd+S).

Validation & Errors

- Inline messages on fields; block committing invalid changes.
- Deletion confirmations (quiz, question).
- Clear dialogs for IO/JSON errors and import conflicts.

Build & Forms Instrumentation (headless Gradle)

- Dependencies already added: com.jetbrains.intellij.java:java-compiler-ant-tasks and com.jetbrains.intellij.java:java-gui-forms-rt.
- Gradle tasks (to implement):
    - Define Ant taskdef for javac2/forms instrumentation from java-compiler-ant-tasks JAR.
    - Add instrumentForms task that runs before compileJava:
        - Inputs: src/main/java and .form files.
        - Outputs: instrumented classes into Gradle’s classes dir.
        - Ensure forms_rt is on compile and runtime classpath.
    - Wire task ordering: compileJava.dependsOn(instrumentForms), or instrument after compile into classes dir (both are common patterns).
- Modular runtime (jlink/jpackage):
    - forms_rt is not modular; configure org.beryx.jlink to merge classpath jars (force-merge) so the runtime image includes forms_rt.
    - Verify app starts from image and jpackage bundle with forms UI.

Testing (JUnit 5)

- Persistence: load/save round-trip; error on malformed JSON; autosave debounce via injected clock/executor.
- Single instance: cannot acquire lock → exit path (simulate via lock held).
- Validation: single vs multiple correct rules; min answers; non-empty names/texts; quiz name uniqueness.
- Import/Export: export schema; import success without collisions; import collision throws and leaves state unchanged.

Milestones

- M1: Models, Jackson repo, file lock, theme; Home tab with list/search/create/rename/delete; Save Now; empty Editor/Practice tabs stubbed.
- M2: Editor tab with .form dialogs, validation rules, reorder questions; autosave (5s).
- M3: Practice flow with settings, immediate feedback, progress, summary, review; retry incorrect.
- M4: Import/Export (dialogs, conflict failure), polish errors/confirmations, minimal shortcuts.
- M5: Tests for persistence, validation, import/export; ensure ./gradlew clean build, run, jlink, jpackage succeed with forms instrumentation.

Acceptance Checklist Alignment

- Create/edit/organize quizzes with validations; practice with immediate feedback and summary; JSON saved under ~/.quizzr/quizzes.json; no backups; second instance blocked; import fails on conflicts; clear
  errors and confirmations.