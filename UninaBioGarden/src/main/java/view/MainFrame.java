package view;

import controller.MainController;
import model.Proprietario;

import javax.swing.*;
import java.util.List;

public class MainFrame extends JFrame{
    //Attributi:
    private JPanel contentPanel;
    private JPanel menuPanel;
    private JSplitPane splitPane;
    private List<JButton> menuButtons;
    private Proprietario proprietario;
    private JButton logoutButton;
    private MainController controller;

    //Metodi:
    public MainFrame(Proprietario proprietario) {
        this.proprietario = proprietario;

        setTitle("UninaBioGarden");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

}
