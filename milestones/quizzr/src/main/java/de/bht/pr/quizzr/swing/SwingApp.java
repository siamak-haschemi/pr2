package de.bht.pr.quizzr.swing;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

import javax.swing.*;
import java.awt.*;

public class SwingApp {
  public static void main(String[] args) {
    EventQueue.invokeLater(
        () -> {
          FlatLightLaf.setup();
          FlatDraculaIJTheme.setup();
          JFrame frame = new JFrame("Quizzr Swing - M1");
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

          JLabel label = new JLabel("Hello Quizzr (Swing + FlatLaf)!", SwingConstants.CENTER);
          JButton start = new JButton("Start");
          start.addActionListener(e -> label.setText("Let's go!"));

          JPanel center = new JPanel(new BorderLayout());
          center.add(label, BorderLayout.CENTER);
          center.add(start, BorderLayout.SOUTH);

          frame.getContentPane().add(center);
          frame.setSize(480, 320);
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);
        });
  }
}
