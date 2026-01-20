package controller;

import model.Proprietario;
import view.*;

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
        JPanel panel = new JPanel();
        switch (panelName) {
            case "I Miei Lotti":
                panel = new LottiPanel(proprietario);
                break;
            case "Progetti Stagionali":
                panel = new ProgettiPanel(proprietario);
                break;
            case "Dashboard":
                //panel = new DashboardPanel(proprietario);
                break;
            case "Notifiche":
                panel = new NotifichePanel(proprietario);
                break;
            case "Catalogo":
                panel = new CatalogoPanel();
                break;
            default:
                panel = new JPanel();
                JLabel label = new JLabel("Sezione" + panelName, SwingConstants.CENTER);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                panel.setLayout(new BorderLayout());
                panel.add(label, BorderLayout.CENTER);
        }
        mainFrame.setContentPanel(panel);
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
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
