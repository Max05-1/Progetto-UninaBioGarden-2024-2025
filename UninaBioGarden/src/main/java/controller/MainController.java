package controller;

import model.Proprietario;
import view.MainFrame;
import view.ProgettiPanel;

import javax.swing.*;
import java.awt.*;

public class MainController {

    private MainFrame mainFrame;
    private Proprietario proprietario;

    public MainController(MainFrame frame, Proprietario proprietario) {
        this.mainFrame = frame;
        this.proprietario = proprietario;
    }

    public void showPanel(String panelName) {
        JPanel panel;
        switch (panelName) {
            case "Progetti Stagionali":
                panel = new ProgettiPanel(proprietario);
                break;

            default:
                panel = new JPanel();
                JLabel label = new JLabel("Sezione" + panelName, SwingConstants.CENTER);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                panel.setLayout(new java.awt.BorderLayout());
                panel.add(label, BorderLayout.CENTER);
        }
        mainFrame.setContentPane(panel);
    }

    public void logout() {
        int conferma = JOptionPane.showConfirmDialog(
                mainFrame,
                "Sei sicuro di voler effettuare il logout?",
                "Conferma Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (conferma == JOptionPane.YES_OPTION) {
            mainFrame.dispose();
            SwingUtilities.invokeLater(() -> new view.LoginFrame().setVisible(true));
        }
    }
}
