package view;

import controller.NotificheController;
import model.Coltivatore;
import model.Notifica;
import model.Proprietario;
import model.Progetto;
import util.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Comparator;

public class NotifichePanel extends JPanel {

    private final Proprietario proprietario;
    private final NotificheController controller;

    private JComboBox<Progetto> comboProgetto;
    private JRadioButton rbTutti;
    private JRadioButton rbSingolo;
    private JComboBox<Coltivatore> comboColtivatore;
    private JComboBox<String> comboTipo;
    private JTextArea txtMessaggio;
    private JTable tbl;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private Map<Integer, String> coltivatoreNomiMap;

    public NotifichePanel(Proprietario proprietario) {
        this.proprietario = proprietario;
        this.controller = new NotificheController();


        this.coltivatoreNomiMap = new HashMap<>();
        try {
            List<Coltivatore> tuttiColtivatori = controller.getAllColtivatori();
            for (Coltivatore c : tuttiColtivatori) {
                coltivatoreNomiMap.put(c.getId(), c.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            handleSQLException("Errore caricamento nomi coltivatori", e);
        }
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.COLORE_SFONDO_ALT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        caricaProgetti();
    }

    private JPanel buildForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.0;
        c.gridx = 0; c.gridy = 0;

        formPanel.add(new JLabel("Progetto:"), c);
        c.gridx = 1; c.weightx = 1.0;
        comboProgetto = new JComboBox<>();
        comboProgetto.addActionListener(e -> {
            caricaColtivatori();
            caricaNotifiche();
        });
        formPanel.add(comboProgetto, c);

        c.gridx = 2; c.weightx = 0.0;
        formPanel.add(new JLabel("Tipo Notifica:"), c);
        c.gridx = 3; c.weightx = 0.5;
        comboTipo = new JComboBox<>(new String[]{"Generica", "Segnalazione Anomalia"});
        formPanel.add(comboTipo, c);

        c.gridx = 0; c.gridy++; c.weightx = 0.0;
        formPanel.add(new JLabel("Destinatario:"), c);
        c.gridx = 1; c.weightx = 1.0; c.gridwidth = 3;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        radioPanel.setOpaque(false);
        rbTutti = new JRadioButton("Tutti i coltivatori", true);
        rbTutti.setOpaque(false);
        rbSingolo = new JRadioButton("Singolo coltivatore:", false);
        rbSingolo.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbTutti); bg.add(rbSingolo);
        radioPanel.add(rbTutti);
        radioPanel.add(rbSingolo);

        comboColtivatore = new JComboBox<>();
        comboColtivatore.setEnabled(false);
        radioPanel.add(comboColtivatore);
        formPanel.add(radioPanel, c);

        ActionListener radioListener = e -> comboColtivatore.setEnabled(rbSingolo.isSelected());
        rbTutti.addActionListener(radioListener);
        rbSingolo.addActionListener(radioListener);
        c.gridwidth = 1;



        c.gridx = 0; c.gridy++; c.gridwidth = 4;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        txtMessaggio = new JTextArea(4, 0);
        txtMessaggio.setFont(Theme.FONT_TESTO_PICCOLO);
        txtMessaggio.setLineWrap(true);
        txtMessaggio.setWrapStyleWord(true);
        JScrollPane spTextArea = new JScrollPane(txtMessaggio);
        spTextArea.setBorder(BorderFactory.createTitledBorder("Testo Notifica"));
        formPanel.add(spTextArea, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0.0;

        c.gridx = 0; c.gridy++; c.gridwidth = 4;
        c.anchor = GridBagConstraints.EAST;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton btnCrea = new JButton("✅ Crea Notifica");
        btnCrea.setToolTipText("Invia la notifica ai destinatari selezionati");
        btnCrea.addActionListener(e -> creaNotifica());
        buttonPanel.add(btnCrea);

        JButton btnRefresh = new JButton("🔄 Aggiorna Elenco");
        btnRefresh.setToolTipText("Ricarica l'elenco delle notifiche inviate");
        btnRefresh.addActionListener(e -> caricaNotifiche());
        buttonPanel.add(btnRefresh);

        formPanel.add(buttonPanel, c);

        return formPanel;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tbl = new JTable(tableModel);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.setAutoCreateRowSorter(true);
        tbl.setFillsViewportHeight(true);
        tbl.setFont(Theme.FONT_TESTO_PICCOLO);
        tbl.getTableHeader().setFont(Theme.FONT_TESTO_PICCOLO.deriveFont(Font.BOLD));
        tbl.setRowHeight(22);

        tableModel.setColumnIdentifiers(new String[]{"Data Invio", "Tipo", "Messaggio", "Coltivatore"});

        sorter = new TableRowSorter<>(tableModel);
        tbl.setRowSorter(sorter);
        sorter.setComparator(0, Comparator.comparing(
                o -> (o instanceof String) ? (String)o : "",
                Comparator.nullsLast(Comparator.naturalOrder())
        ));


        JScrollPane scrollPaneTable = new JScrollPane(tbl);
        scrollPaneTable.getViewport().setBackground(Theme.COLORE_SFONDO_PANEL);
        return scrollPaneTable;
    }


    private void caricaProgetti() {
        try {
            List<Progetto> progetti = controller.caricaProgetti(proprietario.getId());
            Object selected = comboProgetto.getSelectedItem();
            comboProgetto.removeAllItems();

            if (progetti.isEmpty()) {
                comboProgetto.setEnabled(false);
                comboProgetto.addItem(null);
                comboColtivatore.removeAllItems();
                comboColtivatore.setEnabled(false);
                rbSingolo.setEnabled(false);
                rbTutti.setSelected(true);
                caricaNotifiche();
            } else {
                comboProgetto.setEnabled(true);
                rbSingolo.setEnabled(true);
                for (Progetto p : progetti) comboProgetto.addItem(p);

                if (selected instanceof Progetto && progetti.contains(selected)) {
                    comboProgetto.setSelectedItem(selected);
                } else {
                    comboProgetto.setSelectedIndex(0);
                }
                if (Objects.equals(selected, comboProgetto.getSelectedItem())) {
                    caricaColtivatori();
                    caricaNotifiche();
                }
            }
        } catch (SQLException ex) {
            handleSQLException("Errore caricamento progetti", ex);
            comboProgetto.removeAllItems();
            comboProgetto.setEnabled(false);
            comboProgetto.addItem(null);
            comboColtivatore.removeAllItems();
            comboColtivatore.setEnabled(false);
            rbSingolo.setEnabled(false);
            rbTutti.setSelected(true);
            caricaNotifiche();
        }
    }

    private void caricaColtivatori() {
        Progetto p = getSelectedProgetto();
        if (p == null) {
            comboColtivatore.removeAllItems();
            comboColtivatore.setEnabled(false);
            rbSingolo.setEnabled(false);
            rbTutti.setSelected(true);
            return;
        }
        try {
            List<Coltivatore> cols = controller.caricaColtivatoriByProgetto(p.getId());
            Object selected = comboColtivatore.getSelectedItem();
            comboColtivatore.removeAllItems();

            if (cols.isEmpty()) {
                comboColtivatore.setEnabled(false);
                comboColtivatore.addItem(null); // Placeholder null
                rbSingolo.setEnabled(false);
                rbTutti.setSelected(true);
            } else {
                comboColtivatore.setEnabled(rbSingolo.isSelected());
                rbSingolo.setEnabled(true);
                for (Coltivatore c : cols) comboColtivatore.addItem(c);

                if (selected instanceof Coltivatore && cols.contains(selected)) {
                    comboColtivatore.setSelectedItem(selected);
                } else {
                    comboColtivatore.setSelectedIndex(0);
                }
            }
        } catch (SQLException ex) {
            handleSQLException("Errore caricamento coltivatori", ex);
            comboColtivatore.removeAllItems();
            comboColtivatore.setEnabled(false);
            comboColtivatore.addItem(null);
            rbSingolo.setEnabled(false);
            rbTutti.setSelected(true);
        }
    }

    private void creaNotifica() {
        Progetto p = getSelectedProgetto();
        if (p == null) return;

        String tipo = (String) comboTipo.getSelectedItem();
        String msg = txtMessaggio.getText().trim();

        if (tipo == null) { showWarning("Seleziona un tipo di notifica."); return; }
        if (msg.isEmpty()) { showWarning("Inserisci il testo della notifica."); txtMessaggio.requestFocus(); return; }

        Integer idColt = null;
        if (rbSingolo.isSelected()) {
            Object selectedColt = comboColtivatore.getSelectedItem();
            if (!(selectedColt instanceof Coltivatore)) {
                showWarning("Seleziona un coltivatore specifico o scegli 'Tutti'."); return;
            }
            idColt = ((Coltivatore) selectedColt).getId();
        }

        Date dataInvio = new Date();

        try {
            if ("Generica".equals(tipo)) {
                controller.creaNotificaAttivitaImminente(proprietario.getId(), p.getId(), idColt, msg, dataInvio);
            } else {
                controller.creaNotificaAnomalia(proprietario.getId(), p.getId(), idColt, msg, dataInvio);
            }
            showInfo("Notifica creata con successo!");
            caricaNotifiche();
            txtMessaggio.setText("");
        } catch (SQLException ex) {
            handleSQLException("Errore durante la creazione della notifica", ex);
        }
    }

    private void caricaNotifiche() {
        Progetto p = getSelectedProgetto();
        if (p == null) {
            tableModel.setRowCount(0);
            return;
        }

        try {
            List<Notifica> list = controller.caricaNotifichePerProgetto(p.getId());
            tableModel.setRowCount(0);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (Notifica n : list) {

                String nomeColtivatore;
                Integer idColtivatore = n.getIdColtivatore(); //

                if (idColtivatore == null) {
                    nomeColtivatore = "Tutti";
                } else {
                    nomeColtivatore = coltivatoreNomiMap.getOrDefault(
                            idColtivatore,
                            "ID: " + idColtivatore
                    );
                }

                tableModel.addRow(new Object[]{
                        n.getDataInvio() != null ? dateFormat.format(n.getDataInvio()) : "N/D",
                        n.getTipoNotifica(),
                        n.getMessaggio(),
                        nomeColtivatore
                });
            }
            if(tbl.getRowSorter() != null && tbl.getRowCount() > 0) {
                List<RowSorter.SortKey> sortKeys = List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING));
                tbl.getRowSorter().setSortKeys(sortKeys);
            }


        } catch (SQLException ex) {
            handleSQLException("Errore caricamento notifiche", ex);
            tableModel.setRowCount(0);
        }
    }

    private Progetto getSelectedProgetto() {
        Object selected = comboProgetto.getSelectedItem();
        if (selected instanceof Progetto) {
            return (Progetto) selected;
        }
        if (comboProgetto.isEnabled() && comboProgetto.getItemCount() > 0 && selected != null) {
            showWarning("Seleziona un progetto valido dall'elenco.");
        }
        return null;
    }

    private void handleSQLException(String messagePrefix, SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, messagePrefix + ": " + ex.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Attenzione", JOptionPane.WARNING_MESSAGE);
    }
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Informazione", JOptionPane.INFORMATION_MESSAGE);
    }

}