package view;

import controller.ProgettoController;
import model.Attivita;
import model.Coltura;
import model.Proprietario;
import model.Progetto;
import model.Coltivatore;
import util.Theme;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.toedter.calendar.JDateChooser;

public class DettaglioProgettoPanel extends JPanel {

    private Progetto                            progetto;
    private Proprietario                        proprietario;
    private Map<Attivita, JComboBox<String>>    comboMap;
    private Runnable                            onCloseCallback;
    private ProgettoController                  controller;
    private Map<Integer, String>                coltivatoreNomiMap;

    private JPanel      contentPanel;
    private JScrollPane scrollPane;
    private boolean modificheAggiunte = false;

    public DettaglioProgettoPanel(Progetto progetto, Proprietario proprietario, ProgettoController controller, Runnable onCloseCallback) {
        this.progetto = progetto;
        this.proprietario = proprietario;
        this.controller = controller;
        this.onCloseCallback = onCloseCallback;
        this.comboMap = new HashMap<>();
        this.coltivatoreNomiMap = new HashMap<>();

        try {
            List<Coltivatore> tuttiColtivatori = controller.getAllColtivatori();
            for (Coltivatore c : tuttiColtivatori) {
                coltivatoreNomiMap.put(c.getId(), c.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore caricamento nomi coltivatori: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }

        inizializzaUI();
        aggiornaContenutoColture();
    }

    private void inizializzaUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.COLORE_SFONDO_ALT);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton btnAggiungiColtura = new JButton("Aggiungi Coltura");
        btnAggiungiColtura.setToolTipText("Aggiungi una nuova coltura a questo progetto");
        btnAggiungiColtura.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAggiungiColtura.addActionListener(e -> apriDialogAggiungiColtura());
        buttonPanel.add(btnAggiungiColtura);

        JButton btnSalva = new JButton("Salva Modifiche");
        btnSalva.setToolTipText("Salva le modifiche agli stati delle attività");
        btnSalva.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalva.addActionListener(e -> salvaModifiche());
        buttonPanel.add(btnSalva);

        JButton btnElimina = new JButton("Elimina Progetto");
        btnElimina.setToolTipText("Elimina l'intero progetto e le sue attività");
        btnElimina.setForeground(Theme.COLORE_ERRORE);
        btnElimina.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnElimina.addActionListener(e -> eliminaProgetto());
        buttonPanel.add(btnElimina);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void aggiornaContenutoColture() {
        contentPanel.removeAll();

        try {
            List<Coltura> colture = controller.getColturePerProgetto(progetto.getId());

            if (colture.isEmpty()) {
                JLabel emptyLabel = new JLabel("Nessuna coltura associata a questo progetto.");
                emptyLabel.setFont(Theme.FONT_TESTO_NORMALE);
                emptyLabel.setForeground(Theme.COLORE_TESTO_GRIGIO);
                contentPanel.add(emptyLabel);
            } else {
                for (Coltura c : colture) {
                    contentPanel.add(createColturaPanel(c));
                    contentPanel.add(Box.createVerticalStrut(15));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore caricamento dati: " + e.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
            contentPanel.add(new JLabel("Errore nel caricamento delle colture."));
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }


    private JPanel createColturaPanel(Coltura coltura) throws SQLException {
        JPanel colturaPanel = new JPanel();
        colturaPanel.setLayout(new BoxLayout(colturaPanel, BoxLayout.Y_AXIS));
        TitledBorder titledBorder = BorderFactory.createTitledBorder(coltura.getNome());
        titledBorder.setTitleFont(Theme.FONT_TESTO_NORMALE.deriveFont(Font.BOLD));
        titledBorder.setTitleColor(Theme.COLORE_TESTO_SCURO);
        colturaPanel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(5, 10, 10, 10)
        ));
        colturaPanel.setOpaque(false);

        List<Attivita> attivitaList = controller.getAttivitaPerColtura(progetto.getId(), coltura.getId());

        if (attivitaList.isEmpty()) {
            JLabel emptyAttivita = new JLabel("Nessuna attività pianificata per questa coltura.");
            emptyAttivita.setFont(Theme.FONT_TESTO_PICCOLO_ITALIC);
            emptyAttivita.setForeground(Theme.COLORE_TESTO_GRIGIO);
            colturaPanel.add(emptyAttivita);
        } else {
            for (Attivita a : attivitaList) {
                colturaPanel.add(createAttivitaRow(a));
            }
        }
        return colturaPanel;
    }

    private JPanel createAttivitaRow(Attivita attivita) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row.setOpaque(false);

        JLabel tipoLabel = new JLabel("Tipo: " + attivita.getTipo());
        tipoLabel.setFont(Theme.FONT_TESTO_DETTAGLIO);
        tipoLabel.setForeground(Theme.COLORE_TESTO_SCURO);
        row.add(tipoLabel);

        String nomeColtivatore = coltivatoreNomiMap.getOrDefault(
                attivita.getIdColtivatore(),
                "ID: " + attivita.getIdColtivatore()
        );
        JLabel coltivatoreLabel = new JLabel("Coltivatore: " + nomeColtivatore);

        coltivatoreLabel.setFont(Theme.FONT_TESTO_DETTAGLIO);
        coltivatoreLabel.setForeground(Theme.COLORE_TESTO_GRIGIO);
        row.add(coltivatoreLabel);

        row.add(Box.createHorizontalStrut(10));

        JLabel statoLabel = new JLabel("Stato:");
        statoLabel.setFont(Theme.FONT_TESTO_DETTAGLIO);
        row.add(statoLabel);

        String[] stati = {"pianificata", "in corso", "completata"};
        JComboBox<String> statoCombo = new JComboBox<>(stati);
        statoCombo.setFont(Theme.FONT_TESTO_DETTAGLIO);
        statoCombo.setSelectedItem(attivita.getStato());
        statoCombo.setToolTipText("Modifica lo stato di avanzamento dell'attività");
        row.add(statoCombo);

        comboMap.put(attivita, statoCombo);

        return row;
    }


    private void salvaModifiche() {
        boolean modificheEffettuate = false;
        boolean progettoAppenaChiuso = false;
        try {
            for (Map.Entry<Attivita, JComboBox<String>> entry : comboMap.entrySet()) {
                Attivita a = entry.getKey();
                String nuovoStato = (String) entry.getValue().getSelectedItem();

                if (nuovoStato != null && !nuovoStato.equals(a.getStato())) {

                    if (controller.aggiornaStatoAttivita(a.getId(), nuovoStato)) {
                        progettoAppenaChiuso = true;
                    }
                    a.setStato(nuovoStato);
                    modificheEffettuate = true;
                }
            }

            if (modificheEffettuate || this.modificheAggiunte) {

                if (progettoAppenaChiuso) {
                    JOptionPane.showMessageDialog(this,
                            "Modifiche salvate con successo! Tutte le attività sono complete e il progetto è stato chiuso.",
                            "Progetto Completato", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Modifiche salvate con successo!", "Salvataggio Completato", JOptionPane.INFORMATION_MESSAGE);
                }

                if (onCloseCallback != null) onCloseCallback.run();
                closeDialog();

            } else {
                JOptionPane.showMessageDialog(this, "Nessuna modifica da salvare.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore durante il salvataggio delle modifiche: " + ex.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminaProgetto() {
        int conferma = JOptionPane.showConfirmDialog(
                this,
                "Sei sicuro di voler eliminare definitivamente il progetto \"" + progetto.getNome() + "\"?\nQuesta azione non può essere annullata.",
                "Conferma Eliminazione Progetto",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (conferma == JOptionPane.YES_OPTION) {
            try {
                controller.eliminaProgetto(progetto.getId());
                JOptionPane.showMessageDialog(this, "Progetto eliminato con successo!", "Eliminazione Completata", JOptionPane.INFORMATION_MESSAGE);

                if (onCloseCallback != null) onCloseCallback.run();
                closeDialog();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Errore durante l'eliminazione del progetto: " + ex.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void closeDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JDialog) {
            ((JDialog) window).dispose();
        }
    }


    private void apriDialogAggiungiColtura() {
        List<Coltura> coltureDisponibili;
        List<Coltivatore> tuttiColtivatori;
        try {
            coltureDisponibili = controller.getColtureAggiungibili(progetto.getId(), progetto.getStagione());
            tuttiColtivatori = controller.getAllColtivatori();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore nel caricamento delle opzioni: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (coltureDisponibili.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tutte le colture adatte per la stagione '" + progetto.getStagione() + "' sono già state aggiunte a questo progetto.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPanel popupPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        JComboBox<Coltura> comboColtura = new JComboBox<>(coltureDisponibili.toArray(new Coltura[0]));
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"semina", "irrigazione", "raccolta"});
        JComboBox<Coltivatore> comboColtivatore = new JComboBox<>(tuttiColtivatori.toArray(new Coltivatore[0]));

        JDateChooser datePicker = new JDateChooser();
        datePicker.setDateFormatString("yyyy-MM-dd");
        datePicker.setMinSelectableDate(progetto.getDataInizio());
        datePicker.setMaxSelectableDate(progetto.getDataFine());
        datePicker.setDate(progetto.getDataInizio());

        JTextField txtQuantita = new JTextField("0.0");
        txtQuantita.setEnabled(false);

        comboTipo.addActionListener(e -> {
            boolean isRaccolta = "raccolta".equals(comboTipo.getSelectedItem());
            txtQuantita.setEnabled(isRaccolta);
        });

        popupPanel.add(new JLabel("Coltura:"));
        popupPanel.add(comboColtura);
        popupPanel.add(new JLabel("Prima Attività:"));
        popupPanel.add(comboTipo);
        popupPanel.add(new JLabel("Assegna a:"));
        popupPanel.add(comboColtivatore);
        popupPanel.add(new JLabel("Data Attività:"));
        popupPanel.add(datePicker);
        popupPanel.add(new JLabel("Q.tà Prevista (solo raccolta):"));
        popupPanel.add(txtQuantita);

        int result = JOptionPane.showConfirmDialog(
                this, popupPanel, "Aggiungi Coltura al Progetto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                Coltura colturaSel = (Coltura) comboColtura.getSelectedItem();
                String tipoSel = (String) comboTipo.getSelectedItem();
                Coltivatore coltivatoreSel = (Coltivatore) comboColtivatore.getSelectedItem();
                java.util.Date dataSelUtil = datePicker.getDate();
                String quantitaStr = txtQuantita.getText();

                controller.validaEAggiungiColtura(
                        progetto, colturaSel, tipoSel,
                        coltivatoreSel, dataSelUtil, quantitaStr
                );

                JOptionPane.showMessageDialog(this, "Coltura '" + colturaSel.getNome() + "' aggiunta con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);

                this.modificheAggiunte = true;
                aggiornaContenutoColture();

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore Input", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                if (ex.getSQLState().equals("23505")) {
                    JOptionPane.showMessageDialog(this, "Errore: La coltura '" + ((Coltura)comboColtura.getSelectedItem()).getNome() + "' è già presente in questo progetto.", "Errore Database", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Errore Database: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}