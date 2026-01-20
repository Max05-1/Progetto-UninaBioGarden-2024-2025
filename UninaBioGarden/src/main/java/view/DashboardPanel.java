package view;

import controller.DashboardController;
import model.Lotto;
import model.Proprietario;
import model.ReportRaccolta;
import util.Theme;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class DashboardPanel extends JPanel {

    private Proprietario proprietario;
    private JComboBox<Lotto> lottoCombo;
    private JTable table;
    private DefaultCategoryDataset dataset;

    private JLabel lblQuantitaMedia;
    private JLabel lblQuantitaMinima;
    private JLabel lblQuantitaMassima;

    private DashboardController controller;

    public DashboardPanel(Proprietario proprietario) {
        this.proprietario = proprietario;
        this.controller = new DashboardController(proprietario.getId());

        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.COLORE_SFONDO_ALT);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Selezione Lotto in alto ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        lottoCombo = new JComboBox<>(); // JComboBox<Lotto>
        lottoCombo.addActionListener(e -> aggiornaReport());
        topPanel.add(new JLabel("Statistiche per Lotto:"));
        topPanel.add(lottoCombo);
        add(topPanel, BorderLayout.NORTH);

        // --- Tabella Dati Raccolta ---
        table = new JTable();
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setFont(Theme.FONT_TESTO_PICCOLO);
        table.getTableHeader().setFont(Theme.FONT_TESTO_PICCOLO.deriveFont(Font.BOLD));
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.getViewport().setBackground(Theme.COLORE_SFONDO_PANEL);

        // --- Card riassuntive ---
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(1, 3, 15, 0));
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

        lblQuantitaMedia = createCard("Media Raccolta / Coltura", "N/D");
        lblQuantitaMinima = createCard("Minimo Raccolto", "N/D");
        lblQuantitaMassima = createCard("Massimo Raccolto", "N/D");

        cardPanel.add(lblQuantitaMedia.getParent().getParent());
        cardPanel.add(lblQuantitaMinima.getParent().getParent());
        cardPanel.add(lblQuantitaMassima.getParent().getParent());

        // --- Grafico a Barre ---
        dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(
                "Quantità Media Raccolta per Coltura", "Coltura", "Quantità Media", dataset);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Theme.COLORE_SFONDO_PANEL);
        plot.setDomainGridlinePaint(Theme.COLORE_BORDO_STANDARD);
        plot.setRangeGridlinePaint(Theme.COLORE_BORDO_STANDARD);
        ((BarRenderer) plot.getRenderer()).setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        plot.getRenderer().setSeriesPaint(0, Theme.COLORE_PRIMARIO);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createLineBorder(Theme.COLORE_BORDO_STANDARD));

        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setOpaque(false);
        leftPanel.add(tableScroll, BorderLayout.CENTER);
        leftPanel.add(cardPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, chartPanel);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);
        splitPane.setDividerSize(5);
        splitPane.setOpaque(false);
        add(splitPane, BorderLayout.CENTER);

        caricaLotti();
    }

    private JLabel createCard(String titolo, String valoreIniziale) {
        JPanel cardContent = new JPanel();
        cardContent.setLayout(new BoxLayout(cardContent, BoxLayout.Y_AXIS));
        cardContent.setOpaque(false);

        JLabel lblTitolo = new JLabel(titolo, SwingConstants.CENTER);
        lblTitolo.setFont(Theme.FONT_TESTO_DETTAGLIO.deriveFont(Font.BOLD));
        lblTitolo.setForeground(Theme.COLORE_TESTO_SCURO);
        lblTitolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValore = new JLabel(valoreIniziale, SwingConstants.CENTER);
        lblValore.setFont(Theme.FONT_TITOLO_SEZIONE);
        lblValore.setForeground(Theme.COLORE_PRIMARIO);
        lblValore.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardContent.add(lblTitolo);
        cardContent.add(Box.createVerticalStrut(5));
        cardContent.add(lblValore);

        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLORE_BORDO_STANDARD, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        cardWrapper.setBackground(Theme.COLORE_SFONDO_CARD);
        cardWrapper.add(cardContent, BorderLayout.CENTER);

        return lblValore;
    }

    private void caricaLotti() {
        try {
            List<Lotto> lotti = controller.getLottiProprietario();
            Object selected = lottoCombo.getSelectedItem();
            lottoCombo.removeAllItems();

            if (lotti.isEmpty()) {
                lottoCombo.setEnabled(false);
                lottoCombo.addItem(null);
                aggiornaReport();
            } else {
                lottoCombo.setEnabled(true);
                for (Lotto l : lotti) {
                    lottoCombo.addItem(l);
                }
                if (selected instanceof Lotto && lotti.contains(selected)) {
                    lottoCombo.setSelectedItem(selected);
                } else {
                    lottoCombo.setSelectedIndex(0);
                }
                if (Objects.equals(selected, lottoCombo.getSelectedItem())) {
                    aggiornaReport();
                }
            }
        } catch (SQLException e) {
            handleSQLException("Errore caricamento lotti", e);
            lottoCombo.removeAllItems();
            lottoCombo.setEnabled(false);
            lottoCombo.addItem(null);
            aggiornaReport();
        }
    }

    private void aggiornaReport() {
        Lotto selected = null;
        if (lottoCombo.getSelectedItem() instanceof Lotto) {
            selected = (Lotto) lottoCombo.getSelectedItem();
        }

        if (selected == null) {
            tableModel().setRowCount(0); // Svuota tabella
            tableModel().setColumnIdentifiers(new Object[]{"Coltura", "N. Raccolte", "Media", "Min", "Max"});
            dataset.clear();
            lblQuantitaMedia.setText("N/D");
            lblQuantitaMinima.setText("N/D");
            lblQuantitaMassima.setText("N/D");
            return;
        }

        try {
            List<ReportRaccolta> reportList = controller.getReportPerLotto(selected.getId());
            String[] columnNames = {"Coltura", "N. Raccolte", "Q.tà Media", "Q.tà Min", "Q.tà Max"};
            Object[][] data = new Object[reportList.size()][5];
            dataset.clear();

            double sommaMedie = 0;
            double minGenerale = Double.MAX_VALUE;
            double maxGenerale = Double.MIN_VALUE;
            boolean hasData = false;

            for (int i = 0; i < reportList.size(); i++) {
                ReportRaccolta r = reportList.get(i);
                data[i][0] = r.getNomeColtura();
                data[i][1] = r.getNumeroRaccolte();
                data[i][2] = formatDouble(r.getQuantitaMedia());
                data[i][3] = formatDouble(r.getQuantitaMinima());
                data[i][4] = formatDouble(r.getQuantitaMassima());

                if (r.getQuantitaMedia() > 0) {
                    dataset.addValue(r.getQuantitaMedia(), "Quantità Media", r.getNomeColtura());
                    sommaMedie += r.getQuantitaMedia();
                    hasData = true;
                }
                if (r.getQuantitaMinima() < minGenerale) {
                    minGenerale = r.getQuantitaMinima();
                    hasData = true;
                }
                if (r.getQuantitaMassima() > maxGenerale) {
                    maxGenerale = r.getQuantitaMassima();
                    hasData = true;
                }
            }

            if (hasData) {
                double mediaGenerale = (reportList.isEmpty() || sommaMedie == 0) ? 0 : sommaMedie / reportList.size();
                lblQuantitaMedia.setText(String.format("%.2f", mediaGenerale));
                lblQuantitaMinima.setText(String.format("%.2f", minGenerale == Double.MAX_VALUE ? 0 : minGenerale));
                lblQuantitaMassima.setText(String.format("%.2f", maxGenerale == Double.MIN_VALUE ? 0 : maxGenerale));
            } else {
                lblQuantitaMedia.setText("0.00");
                lblQuantitaMinima.setText("0.00");
                lblQuantitaMassima.setText("0.00");
            }

            tableModel().setDataVector(data, columnNames);

        } catch (SQLException e) {
            handleSQLException("Errore caricamento report", e);
            tableModel().setRowCount(0); // Svuota tabella
            tableModel().setColumnIdentifiers(new Object[]{"Coltura", "N. Raccolte", "Media", "Min", "Max"}); // Reimposta header
            dataset.clear();
            lblQuantitaMedia.setText("Errore");
            lblQuantitaMinima.setText("Errore");
            lblQuantitaMassima.setText("Errore");
        }
    }

    private DefaultTableModel tableModel() {
        if (table.getModel() instanceof DefaultTableModel) {
            return (DefaultTableModel) table.getModel();
        } else {
            DefaultTableModel newModel = new DefaultTableModel(new Object[]{"Coltura", "N. Raccolte", "Media", "Min", "Max"}, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            table.setModel(newModel);
            return newModel;
        }
    }


    private String formatDouble(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value) || value < 0 ){
            return "N/D";
        }
        return String.format(java.util.Locale.ITALIAN, "%.2f", value);
    }

    private void handleSQLException(String messagePrefix, SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, messagePrefix + ": " + ex.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
    }

}