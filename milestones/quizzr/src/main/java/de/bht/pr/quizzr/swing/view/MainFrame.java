package de.bht.pr.quizzr.swing.view;

import de.bht.pr.quizzr.swing.model.QuizCollection;
import de.bht.pr.quizzr.swing.service.AutosaveService;
import de.bht.pr.quizzr.swing.service.ImportExportService;
import de.bht.pr.quizzr.swing.viewmodel.EditorViewModel;
import de.bht.pr.quizzr.swing.viewmodel.HomeViewModel;
import de.bht.pr.quizzr.swing.viewmodel.PracticeViewModel;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.*;

public class MainFrame extends JFrame {
  private final HomeViewModel homeViewModel;
  private final EditorViewModel editorViewModel;
  private final PracticeViewModel practiceViewModel;
  private final AutosaveService autosaveService;
  private final ImportExportService importExportService;

  private JTabbedPane tabbedPane;
  private HomePanel homePanel;
  private EditorPanel editorPanel;
  private PracticePanelNew practicePanel;

  public MainFrame(
      QuizCollection collection,
      HomeViewModel homeViewModel,
      EditorViewModel editorViewModel,
      PracticeViewModel practiceViewModel,
      AutosaveService autosaveService,
      ImportExportService importExportService) {
    this.homeViewModel = homeViewModel;
    this.editorViewModel = editorViewModel;
    this.practiceViewModel = practiceViewModel;
    this.autosaveService = autosaveService;
    this.importExportService = importExportService;

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

    homePanel = new HomePanel(homeViewModel, editorViewModel);
    editorPanel = new EditorPanel(editorViewModel);
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
    importItem.addActionListener(e -> importQuizzes());

    JMenuItem exportItem = new JMenuItem("Export...");
    exportItem.addActionListener(e -> exportQuizzes());

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
      homeViewModel.saveNow();
      JOptionPane.showMessageDialog(
          this, "Saved successfully", "Save", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(
          this, "Save failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void importQuizzes() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(
        new javax.swing.filechooser.FileNameExtensionFilter("JSON files", "json"));
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      try {
        de.bht.pr.quizzr.swing.util.Result<de.bht.pr.quizzr.swing.model.QuizCollection, String>
            result =
                importExportService.importFromFile(
                    chooser.getSelectedFile().toPath(),
                    homeViewModel.getFilteredQuizzes().isEmpty()
                        ? new de.bht.pr.quizzr.swing.model.QuizCollection()
                        : getCollectionFromViewModel());

        if (result.isFailure()) {
          JOptionPane.showMessageDialog(
              this,
              "Import failed: " + result.getError().orElse("Unknown error"),
              "Import Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        de.bht.pr.quizzr.swing.model.QuizCollection imported = result.getValue().orElse(null);
        if (imported != null) {
          de.bht.pr.quizzr.swing.model.QuizCollection merged =
              importExportService.mergeCollections(getCollectionFromViewModel(), imported);

          // Update the collection
          getCollectionFromViewModel().getQuizzes().clear();
          getCollectionFromViewModel().getQuizzes().addAll(merged.getQuizzes());

          homeViewModel.refresh();
          autosaveService.scheduleAutosave(getCollectionFromViewModel());

          JOptionPane.showMessageDialog(
              this,
              "Successfully imported " + imported.getQuizzes().size() + " quiz(zes)",
              "Import Successful",
              JOptionPane.INFORMATION_MESSAGE);
        }
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            this, "Import failed: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void exportQuizzes() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(
        new javax.swing.filechooser.FileNameExtensionFilter("JSON files", "json"));
    chooser.setSelectedFile(new java.io.File("quizzes-export.json"));
    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      try {
        importExportService.exportToFile(
            getCollectionFromViewModel(), chooser.getSelectedFile().toPath());
        JOptionPane.showMessageDialog(
            this,
            "Successfully exported to " + chooser.getSelectedFile().getName(),
            "Export Successful",
            JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            this, "Export failed: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private de.bht.pr.quizzr.swing.model.QuizCollection getCollectionFromViewModel() {
    de.bht.pr.quizzr.swing.model.QuizCollection collection =
        new de.bht.pr.quizzr.swing.model.QuizCollection();
    collection.getQuizzes().addAll(homeViewModel.getFilteredQuizzes());
    return collection;
  }

  private void exit() {
    int choice =
        JOptionPane.showConfirmDialog(
            this, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
    if (choice == JOptionPane.YES_OPTION) {
      autosaveService.shutdown();
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
                  editorPanel.deleteQuestionAction();
                }
              }
            });
  }
}
