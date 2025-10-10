package de.bht.pr.quizzr.swing.app;

import de.bht.pr.quizzr.swing.editor.QuestionsPanel;
import de.bht.pr.quizzr.swing.editor.QuizEditorPanel;
import de.bht.pr.quizzr.swing.editor.QuizEditorViewModel;
import de.bht.pr.quizzr.swing.importexport.ImportExportView;
import de.bht.pr.quizzr.swing.importexport.ImportExportViewModel;
import de.bht.pr.quizzr.swing.practice.PracticePanelNew;
import de.bht.pr.quizzr.swing.practice.PracticeSummaryDialog;
import de.bht.pr.quizzr.swing.practice.PracticeViewModel;
import de.bht.pr.quizzr.swing.quiz.QuizManagerView;
import de.bht.pr.quizzr.swing.quiz.QuizManagerViewModel;
import de.bht.pr.quizzr.swing.quiz.QuizzRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class AppView extends JFrame {
  private final QuizManagerViewModel quizManagerViewModel;
  private final QuizEditorViewModel quizEditorViewModel;
  private final PracticeViewModel practiceViewModel;
  private final ImportExportViewModel importExportViewModel;

  private JTabbedPane tabbedPane;
  private QuizManagerView homePanel;
  private JPanel editorPanel;
  private QuizEditorPanel quizEditorPanel;
  private QuestionsPanel questionsPanel;
  private PracticePanelNew practicePanel;
  private ImportExportView importExportView;

  public AppView(
      QuizzRepository repository,
      QuizManagerViewModel homeViewModel,
      QuizEditorViewModel editorViewModel,
      PracticeViewModel practiceViewModel,
      ImportExportViewModel importExportViewModel) {
    this.quizManagerViewModel = homeViewModel;
    this.quizEditorViewModel = editorViewModel;
    this.practiceViewModel = practiceViewModel;
    this.importExportViewModel = importExportViewModel;

    initializeUI();
    setupMenuBar();
    setupWindowListener();
    setupKeyboardShortcuts();
  }

  private void initializeUI() {
    setTitle("Quizzr");
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(1000, 700);
    setLocationRelativeTo(null);

    tabbedPane = new JTabbedPane();

    homePanel = new QuizManagerView(quizManagerViewModel, quizEditorViewModel);

    importExportView = new ImportExportView(importExportViewModel, homePanel);

    // Create editor sub-tabs
    JTabbedPane editorTabs = new JTabbedPane();
    quizEditorPanel = new QuizEditorPanel(quizEditorViewModel, quizManagerViewModel);
    questionsPanel = new QuestionsPanel(quizEditorViewModel);
    editorTabs.addTab("Quiz Properties", quizEditorPanel);
    editorTabs.addTab("Questions", questionsPanel);
    editorPanel = new JPanel(new BorderLayout());
    editorPanel.add(editorTabs, BorderLayout.CENTER);

    practicePanel = new PracticePanelNew(practiceViewModel);

    tabbedPane.addTab("Home", homePanel);
    tabbedPane.addTab("Editor", editorPanel);
    tabbedPane.addTab("Practice", practicePanel);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(tabbedPane, BorderLayout.CENTER);
  }

  private void setupMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu("File");
    JMenuItem saveNowItem = new JMenuItem("Save Now");
    saveNowItem.addActionListener(e -> saveNow());

    JMenuItem importItem = new JMenuItem("Import...");
    importItem.addActionListener(e -> importExportView.importQuizzes());

    JMenuItem exportItem = new JMenuItem("Export...");
    exportItem.addActionListener(e -> importExportView.exportQuizzes());

    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.addActionListener(e -> exit());

    fileMenu.add(saveNowItem);
    fileMenu.addSeparator();
    fileMenu.add(importItem);
    fileMenu.add(exportItem);
    fileMenu.addSeparator();
    fileMenu.add(exitItem);

    menuBar.add(fileMenu);
    setJMenuBar(menuBar);
  }

  private void setupWindowListener() {
    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            exit();
          }
        });
  }

  private void saveNow() {
    try {
      quizManagerViewModel.saveNow();
      JOptionPane.showMessageDialog(
          this, "Saved successfully", "Save", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(
          this, "Save failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void exit() {
    int choice =
        JOptionPane.showConfirmDialog(
            this, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
    if (choice == JOptionPane.YES_OPTION) {
      dispose();
      System.exit(0);
    }
  }

  public void switchToEditorTab() {
    tabbedPane.setSelectedComponent(editorPanel);
  }

  public void switchToPracticeTab() {
    tabbedPane.setSelectedComponent(practicePanel);
  }

  public void startPracticeWithQuiz(de.bht.pr.quizzr.swing.model.Quiz quiz) {
    practicePanel.startPracticeSession(quiz);
    switchToPracticeTab();
  }

  public void showPracticeSummary(
      int score,
      int total,
      java.util.List<de.bht.pr.quizzr.swing.model.Question> questions,
      java.util.Map<java.util.UUID, Boolean> results) {
    PracticeSummaryDialog dialog =
        new PracticeSummaryDialog(this, score, total, questions, results, practiceViewModel);
    dialog.setVisible(true);
  }

  private void setupKeyboardShortcuts() {
    int shortcutMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

    // Ctrl/Cmd+N - New Quiz (works when on Home tab)
    javax.swing.KeyStroke newQuizKey =
        KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, shortcutMask);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(newQuizKey, "newQuiz");
    getRootPane()
        .getActionMap()
        .put(
            "newQuiz",
            new javax.swing.AbstractAction() {
              @Override
              public void actionPerformed(java.awt.event.ActionEvent e) {
                if (tabbedPane.getSelectedComponent() == homePanel) {
                  homePanel.createQuizAction();
                }
              }
            });

    // Ctrl/Cmd+S - Save Now
    javax.swing.KeyStroke saveKey =
        KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, shortcutMask);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(saveKey, "saveNow");
    getRootPane()
        .getActionMap()
        .put(
            "saveNow",
            new javax.swing.AbstractAction() {
              @Override
              public void actionPerformed(java.awt.event.ActionEvent e) {
                saveNow();
              }
            });

    // Delete key - Delete selected quiz/question
    javax.swing.KeyStroke deleteKey = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(deleteKey, "delete");
    getRootPane()
        .getActionMap()
        .put(
            "delete",
            new javax.swing.AbstractAction() {
              @Override
              public void actionPerformed(java.awt.event.ActionEvent e) {
                if (tabbedPane.getSelectedComponent() == homePanel) {
                  homePanel.deleteQuizAction();
                } else if (tabbedPane.getSelectedComponent() == editorPanel) {
                  questionsPanel.deleteQuestionAction();
                }
              }
            });
  }
}
