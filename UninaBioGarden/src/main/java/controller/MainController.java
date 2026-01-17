package controller;

import model.Proprietario;
import view.MainFrame;

import javax.swing.*;

public class MainController {

    private MainFrame mainFrame;
    private Proprietario proprietario;

    public MainController(MainFrame frame, Proprietario proprietario) {
        this.mainFrame = frame;
        this.proprietario = proprietario;
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
