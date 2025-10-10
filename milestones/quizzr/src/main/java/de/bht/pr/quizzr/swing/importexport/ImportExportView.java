package de.bht.pr.quizzr.swing.importexport;

import de.bht.pr.quizzr.swing.model.QuizCollection;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

public class ImportExportView {

  private final ImportExportViewModel exportViewModel;
  private final Component parent;

  public ImportExportView(ImportExportViewModel exportViewModel, Component parent) {
    this.exportViewModel = exportViewModel;
    this.parent = parent;
  }

  public void importQuizzes() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(
        new javax.swing.filechooser.FileNameExtensionFilter("JSON files", "json"));
    if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
      try {
        final Path path = chooser.getSelectedFile().toPath();
        final QuizCollection result = exportViewModel.importFromPath(path);

        JOptionPane.showMessageDialog(
            parent,
            "Successfully imported " + result.getQuizzes().size() + " quiz(zes)",
            "Import Successful",
            JOptionPane.INFORMATION_MESSAGE);

      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            parent, "Import failed: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public void exportQuizzes() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(
        new javax.swing.filechooser.FileNameExtensionFilter("JSON files", "json"));
    chooser.setSelectedFile(new java.io.File("quizzes-export.json"));
    if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
      try {
        final Path path = chooser.getSelectedFile().toPath();
        exportViewModel.exportToPath(path);
        JOptionPane.showMessageDialog(
            parent,
            "Successfully exported to " + chooser.getSelectedFile().getName(),
            "Export Successful",
            JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            parent, "Export failed: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}
