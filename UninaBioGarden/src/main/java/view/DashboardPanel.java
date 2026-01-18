package view;

import controller.DashboardController;
import model.Lotto;
import model.Proprietario;
import model.ReportRaccolta;
import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class DashboardPanel extends JPanel {
    //Attributi:
    private Proprietario proprietario;
    private JComboBox<Lotto> lottoCombo;
    private JTable table;
    private JLabel lblQuantitaMedia;
    private JLabel lblQuantitaMinima;
    private JLabel lblQuantitaMassima;
    private DashboardController controller;


}
