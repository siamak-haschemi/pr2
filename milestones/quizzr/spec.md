# **Quizzr — Plain-English Product Spec (with JSON persistence)**

A simple desktop app to create, organize, and practice quizzes. This version adds clear rules for saving all data to a **single JSON file** in the user’s home folder.

---

## **Product summary**

* Create and manage quizzes made of questions and answer choices.

* Practice quizzes in a focused session and see results.

* **All data is saved automatically** to a JSON file in a hidden folder in the user’s home directory: `~/.quizzr/`.

---

## **Who it’s for**

* Teachers and trainers preparing practice material.

* Students or self-learners building their own quizzes.

* Anyone who wants quick practice sessions with basic statistics.

---

## **Core concepts**

* **Quiz Collection**: Your library of quizzes.

* **Quiz**: A named set of questions.

* **Question**: Has text, a list of answer choices, a question type, and which choices are correct.

  * **Question Type**:

    * *Single answer* (exactly one correct choice)

    * *Multiple answers* (one or more correct choices)

* **Answer**: An answer choice’s text.

* **Practice Session (Quiz Execution)**: Running through a quiz, answering questions, and getting a result summary.

---

## **Persistence & data (JSON under `~/.quizzr`)**

### **Where and what we save**

* **Folder**: A hidden folder in the user’s home directory named `.quizzr`.

* **Main file**: `~/.quizzr/quizzes.json` — contains the entire **current quiz collection** (all quizzes and their questions).

* **Backups**: Before replacing the main file, the app creates a timestamped backup in the same folder (for example, `quizzes-2025-10-08T19-15-03.bak.json`).

### **When we save**

* **Autosave**: The app saves changes shortly after each edit (rename quiz, add/remove questions/answers, mark correct answers).

* **Manual Save**: There’s also a “Save Now” option for users who prefer to save on demand.

* **On Exit**: The app performs a final save when closing.

### **On startup**

* If the folder and file exist, the app **loads the last saved collection** automatically.

* If not, the app **creates the folder** and starts with an **empty collection**.

### **Data safety & recovery**

* **Backups**: The most recent few backups are kept. Users can restore from a backup via a simple “Restore from Backup” action.

* **Corruption handling**: If the file can’t be read, the app explains the issue in plain language and offers to restore the latest backup or start fresh.

* **Single-user protection**: If a second copy of the app tries to open the same data, it warns the user and opens in read-only mode to avoid overwriting.

### **Privacy**

* Data is stored **locally only**. No cloud or online sync in this version.

---

## **Key user journeys**

### **1\) Home (Quiz Collection)**

* See a list of all quizzes with names and question counts.

* Actions:

  * **Create** a new quiz

  * **Rename**, **Duplicate**, or **Delete** a quiz (with confirmation)

  * **Search/filter** quizzes by name

  * **Save Now** (optional; autosave runs in the background)

**Acceptance checklist**

* Users can see and manage all quizzes in the collection.

* Every change appears in the list and is **saved to `~/.quizzr/quizzes.json`**.

* Deleting shows a confirmation and then saves.

---

### **2\) Create & edit quizzes**

* **Rename quiz**.

* **Add questions** one at a time.

* For each question:

  * Enter **question text**.

  * Choose **type** (single or multiple answers).

  * Add, edit, remove, and reorder **answer choices**.

  * Mark which choices are **correct**.

* **Reorder questions** within the quiz.

* The app prevents invalid setups (for example, a single-answer question must have exactly one correct choice).

**Acceptance checklist**

* Users can fully define quizzes (name, questions, answers, correct answers).

* Invalid setups show friendly messages and block saving that particular change until fixed.

* Changes are autosaved to `~/.quizzr/quizzes.json`.

---

### **3\) Practice a quiz (run a session)**

* Start from a quiz in the list.

* Optional settings:

  * **Order**: original or shuffled

  * **Number of questions**: all or a subset

* During practice:

  * Show **one question at a time** with answer choices.

  * Selection rules match the question type.

  * **Submit** shows immediate feedback (correct/incorrect) and the correct choices.

  * Show progress (e.g., “Question 3 of 10”).

* Finish with a **summary** (score and per-question results).

**Acceptance checklist**

* Users can run a complete session without leaving the screen.

* Feedback is clear and immediate.

* A summary is shown at the end.

---

### **4\) Results & statistics**

* **Summary screen** at the end of a session:

  * **Score**: correct / total and percentage

  * **Per-question breakdown**: which were right or wrong

* **Review mode**:

  * See each question again with the user’s answer and the correct answer(s).

  * Option to **retry incorrect questions** as a quick session (same quiz).

**Acceptance checklist**

* The summary is easy to understand.

* Users can review and optionally retry incorrect ones.

---

### **5\) Import / Export (works with JSON, lives alongside the main file)**

* **Export**: Save the current quiz collection to a user-chosen file (for sharing or backup).  
   *Note:* This is separate from the always-on `~/.quizzr/quizzes.json`.

* **Import**: Bring quizzes in from a JSON file.

  * Default behavior: **Merge** into the current collection.

  * If incoming quiz names collide, the user picks:

    * **Keep both** (the new one is renamed, e.g., “(imported)”)

    * **Replace** existing with imported

* Before any replace, the app makes a **backup** of the current collection.

**Acceptance checklist**

* Export produces a valid JSON file anywhere the user chooses.

* Import merges by default and clearly handles name conflicts.

* The main collection in `~/.quizzr/quizzes.json` is updated and backed up when needed.

---

## **Data rules (kept simple)**

* A **Quiz Collection** has zero or more **Quizzes**.

* A **Quiz** has:

  * A **name**

  * Zero or more **Questions**

* A **Question** has:

  * **Text**

  * A **type** (single or multiple answers)

  * **Two or more** answer choices

  * **Correct answers**:

    * Single: exactly one correct

    * Multiple: one or more correct

* An **Answer** has **text**.

* A **Practice Session**:

  * Belongs to a **Quiz**

  * Produces a **result summary** (score and per-question correctness)

---

## **General usability & guardrails**

* **Undo/redo** for common edits or at least **Cancel** in dialogs.

* **Confirmation** before destructive actions (delete quiz, delete question).

* **Keyboard support** for navigation and answer selection.

* **Accessibility**: clear focus, readable sizes, helpful error messages.

* **Empty states**: friendly guidance when starting from scratch.

---

## **Decisions (set for this version)**

1. **Feedback timing**: Immediate after each question.

2. **Skipping**: Allowed; counts as incorrect.

3. **Session history**: Only the **latest session’s summary** is retained in memory for review; long-term history is **out of scope** for this version.

4. **Import behavior**: **Merge** by default; on name conflicts, user chooses to keep both (rename) or replace.

5. **Saving**: **Autosave on by default**, plus “Save Now”.

6. **Retry incorrect only**: Available right after a session.

7. **Default practice order**: Original order; users can opt into shuffle.

---

## **Acceptance summary (what “done” looks like)**

* Users can create, edit, and organize quizzes with valid questions.

* Users can practice and receive a clear result summary.

* The app **creates `~/.quizzr/` if needed and maintains `quizzes.json`** with autosave and manual save.

* The app **makes timestamped backups** before replacing the main file and can restore from a backup.

* Import/Export work with JSON, safely updating the main collection and protecting user data.

* Clear messages guide the user through errors, confirmations, and recovery.
