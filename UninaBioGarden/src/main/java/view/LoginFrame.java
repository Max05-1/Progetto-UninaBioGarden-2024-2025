package view;

import controller.LoginController;
import model.Proprietario;
import util.Theme;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;

public class LoginFrame extends JFrame {

    private JTextField       usernameField;
    private JPasswordField   passwordField;
    private JButton                 btnLogin;
    private LoginController         controller;

    public LoginFrame() {
        controller = new LoginController();

        setTitle("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ----------------- Pannello principale -----------------
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // ----------------- Colonna sinistra (login) -----------------
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(Theme.COLORE_SFONDO_PANEL);
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.add(loginPanel, BorderLayout.WEST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("Benvenuto");
        titleLabel.setFont(Theme.FONT_TITOLO_PRINCIPALE);
        titleLabel.setForeground(Theme.COLORE_PRIMARIO);
        gbc.gridy = 0;
        loginPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Accedi al tuo account");
        subtitleLabel.setFont(Theme.FONT_TESTO_NORMALE);
        subtitleLabel.setForeground(Theme.COLORE_TESTO_SCURO);
        gbc.gridy = 1;
        loginPanel.add(subtitleLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 20, 0);
        loginPanel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(10, 0, 10, 0);

        usernameField = new JTextField(20);
        usernameField.setFont(Theme.FONT_TESTO_DETTAGLIO);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(Theme.FONT_TESTO_PICCOLO);
        userLabel.setForeground(Theme.COLORE_TESTO_GRIGIO);
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        loginPanel.add(userLabel, gbc);

        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(usernameField, gbc);


        passwordField = new JPasswordField(20);
        passwordField.setFont(Theme.FONT_TESTO_DETTAGLIO);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(Theme.FONT_TESTO_PICCOLO);
        passLabel.setForeground(Theme.COLORE_TESTO_GRIGIO);
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        loginPanel.add(passLabel, gbc);

        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(passwordField, gbc);


        btnLogin = new JButton("Login");
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 0, 10, 0);
        loginPanel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> eseguiLogin());

        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setBackground(Theme.COLORE_SFONDO_MENU);
        mainPanel.add(logoPanel, BorderLayout.CENTER);

        URL logoUrl = getClass().getClassLoader().getResource("icons/logo.png");
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            JLabel logoLabel = new JLabel(icon);
            logoPanel.add(logoLabel);
        } else {
            System.out.println("Logo non trovato! Controlla il percorso.");
            logoPanel.add(new JLabel("Logo non trovato"));
        }

        pack();
        setMinimumSize(new Dimension(700, 450));
        setLocationRelativeTo(null);
    }


    private void eseguiLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Proprietario p = controller.autentica(username, password);
            JOptionPane.showMessageDialog(this, "Benvenuto " + p.getNome() + "!", "Login Riuscito", JOptionPane.INFORMATION_MESSAGE);
            new MainFrame(p).setVisible(true);
            this.dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore Login", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore Database: " + ex.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
        }
    }

}