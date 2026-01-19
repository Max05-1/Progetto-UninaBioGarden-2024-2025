package view;

import controller.MainController;
import model.Proprietario;
import util.Theme;

import javax.swing.ImageIcon;
import java.awt.Image;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private JPanel menuPanel;
    private JSplitPane splitPane;
    private List<JButton> menuButtons;
    private Proprietario proprietario;
    private JButton logoutButton;
    private MainController controller;

    public MainFrame(Proprietario proprietario) {
        this.proprietario = proprietario;

        setTitle("UninaBioGarden");


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        controller = new MainController(this, proprietario);

        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Theme.COLORE_SFONDO_MENU);
        // Imposta una larghezza fissa per il menu
        menuPanel.setPreferredSize(new Dimension(220, 0));
        menuPanel.setMinimumSize(new Dimension(200, 0));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        URL logoUrl = getClass().getClassLoader().getResource("icons/logo.png");
        JLabel logoLabel;
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            Image img = icon.getImage();
            int targetWidth = 130;
            int targetHeight = (int) ((double) img.getHeight(null) / img.getWidth(null) * targetWidth);
            Image scaledImg = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaledImg), SwingConstants.CENTER);
        } else {
            System.out.println("Logo non trovato! Controlla il percorso.");
            logoLabel = new JLabel("UninaBioGarden", SwingConstants.CENTER);
            logoLabel.setFont(Theme.FONT_TITOLO_SEZIONE);
            logoLabel.setForeground(Theme.COLORE_TESTO_SCURO);
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nomeLabel = new JLabel(proprietario.getNome() + " " + proprietario.getCognome());
        nomeLabel.setFont(Theme.FONT_TESTO_NORMALE);
        nomeLabel.setForeground(Theme.COLORE_TESTO_SCURO);
        nomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel(proprietario.getEmail());
        emailLabel.setFont(Theme.FONT_TESTO_PICCOLO);
        emailLabel.setForeground(Theme.COLORE_TESTO_SCURO);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(logoLabel);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(nomeLabel);
        menuPanel.add(emailLabel);
        menuPanel.add(Box.createVerticalStrut(30));

        menuButtons = new ArrayList<>();
        JButton btnDashboard = createMenuButton("📊", "Dashboard", menuButtons);
        JButton btnLotti = createMenuButton("🌱", "I Miei Lotti", menuButtons);
        JButton btnProgetti = createMenuButton("🗓️", "Progetti", menuButtons);
        JButton btnCatalogo = createMenuButton("📖", "Catalogo", menuButtons);
        JButton btnNotifiche = createMenuButton("🔔", "Notifiche", menuButtons);

        JPanel buttonsWrapperPanel = new JPanel();
        buttonsWrapperPanel.setLayout(new BoxLayout(buttonsWrapperPanel, BoxLayout.Y_AXIS));
        buttonsWrapperPanel.setOpaque(false);
        buttonsWrapperPanel.setMaximumSize(new Dimension(180, Integer.MAX_VALUE));

        for (JButton btn : menuButtons) {
            buttonsWrapperPanel.add(btn);
            buttonsWrapperPanel.add(Box.createVerticalStrut(8));
        }
        buttonsWrapperPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(buttonsWrapperPanel);


        menuPanel.add(Box.createVerticalGlue());

        logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(180, 40));
        logoutButton.addActionListener(e -> controller.logout());
        menuPanel.add(logoutButton);
        menuPanel.add(Box.createVerticalStrut(10));

        contentPanel = new JPanel(new BorderLayout());

        contentPanel.setBackground(Theme.COLORE_SFONDO_ALT);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuPanel, contentPanel);
        splitPane.setDividerLocation(220);
        splitPane.setEnabled(false);
        splitPane.setBorder(null);
        splitPane.setDividerSize(1);

        add(splitPane);

        btnDashboard.addActionListener(e -> controller.showPanel("Dashboard"));
        btnLotti.addActionListener(e -> controller.showPanel("I Miei Lotti"));
        btnProgetti.addActionListener(e -> controller.showPanel("Progetti Stagionali"));
        btnCatalogo.addActionListener(e -> controller.showPanel("Catalogo"));
        btnNotifiche.addActionListener(e -> controller.showPanel("Notifiche"));

        controller.showPanel("Dashboard");
        if (!menuButtons.isEmpty()) {
            menuButtons.get(0).doClick();
        }
    }

    public void setContentPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    private JButton createMenuButton(String emoji, String text, List<JButton> allButtons) {
        JButton btn = new JButton(emoji + "  " + text);
        btn.setFont(Theme.FONT_TESTO_NORMALE);
        btn.setForeground(Theme.COLORE_TESTO_SCURO);

        btn.setMinimumSize(new Dimension(0, 40));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            for (JButton b : allButtons) {
                b.setOpaque(false);
                b.setBackground(null);
                b.setFont(Theme.FONT_TESTO_NORMALE);
            }
            btn.setOpaque(true);
            btn.setBackground(Theme.COLORE_SFONDO_CARD.brighter());
            btn.setFont(Theme.FONT_TESTO_NORMALE.deriveFont(Font.BOLD));
        });

        btn.setActionCommand(text);
        allButtons.add(btn);
        return btn;
    }
}