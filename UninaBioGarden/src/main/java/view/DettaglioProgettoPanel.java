package view;

import controller.ProgettoController;
import model.Attivita;
import model.Progetto;
import model.Proprietario;

import javax.swing.*;
import java.util.Map;

public class DettaglioProgettoPanel {
    private Progetto progetto;
    private Proprietario proprietario;
    private Map<Attivita, JComboBox<String>> comboMap;
    private Runnable                            onCloseCallback;
    private ProgettoController controller;
    private Map<Integer, String>                coltivatoreNomiMap;

    private JPanel      contentPanel;
    private JScrollPane scrollPane;
    private boolean modificheAggiunte = false;
}
