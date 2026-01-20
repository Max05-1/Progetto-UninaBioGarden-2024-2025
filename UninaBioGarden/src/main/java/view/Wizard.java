package view;

import controller.WizardController;
import model.Coltura;
import model.Coltivatore;
import model.Lotto;
import model.Proprietario;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.Calendar;

public class Wizard extends JDialog {

    private Proprietario proprietario;
    private List<Lotto> lotti;
    private List<Coltura> colture; // Lista dinamica basata sulla stagione
    private List<Coltivatore> coltivatori;

    private JPanel cardsPanel;
    private CardLayout cardLayout;
    private WizardController controller;

    // Step 1
    private JComboBox<Lotto> lottoCombo;
    private JTextField nomeField;
    private JComboBox<String> stagioneCombo;
    private JDateChooser dataInizioPicker;
    private JDateChooser dataFinePicker;

    // Step 2
    private JPanel step2InnerPanel;
    private JScrollPane step2ScrollPane;
    private List<JCheckBox> coltureCheckboxes;
    private List<JComboBox<String>> tipoAttivitaPerColtura;
    private List<JComboBox<Coltivatore>> coltivatoriPerColtura;
    private List<JTextField> quantitaPerColtura;
    private List<JDateChooser> dataPianificataPerColtura;
    private Map<String, String> emojiPerColtura;

    private JButton btnNext;
    private JButton btnBack;
    private JButton btnFinish;


    public Wizard(JFrame parent, Proprietario proprietario) {
        super(parent, "Wizard Nuovo Progetto", true);
        this.proprietario = proprietario;
        this.controller = new WizardController();

        setLocationRelativeTo(parent);

        try {
            lotti = proprietario != null ? controller.getLottiDisponibili(proprietario.getId()) : new ArrayList<>();
            coltivatori = controller.getAllColtivatori();
            colture = new ArrayList<>();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore caricamento dati iniziali: " + e.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        inizializzaEmoji();

        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);

        cardsPanel.add(createStep1Panel(), "Step1");
        cardsPanel.add(createStep2PanelPlaceholder(), "Step2");

        add(cardsPanel, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        if (stagioneCombo.getItemCount() > 0) {
            stagioneCombo.setSelectedIndex(0);
        } else {
            aggiornaColturePerStagioneSelezionata();
        }

        updateButtons(0);
        pack();
    }

    private void inizializzaEmoji() {
        emojiPerColtura = new HashMap<>();
        emojiPerColtura.put("Carota", "🥕");
        emojiPerColtura.put("Pomodoro", "🍅");
        emojiPerColtura.put("Mais", "🌽");
        emojiPerColtura.put("Insalata", "🥬");
        emojiPerColtura.put("Melanzana", "🍆");
        emojiPerColtura.put("Peperone", "🫑");
        emojiPerColtura.put("Cavolo", "🥦");
        emojiPerColtura.put("Patata", "🥔");
        emojiPerColtura.put("Zucca", "🎃");
        emojiPerColtura.put("Fragola", "🍓");
    }

    // ---------------------- STEP 1 ----------------------
    private JPanel createStep1Panel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lottoCombo = new JComboBox<>();
        if (lotti != null) {
            for (Lotto l : lotti) lottoCombo.addItem(l);
        }

        nomeField = new JTextField();
        stagioneCombo = new JComboBox<>(new String[]{"primavera", "estate", "autunno", "inverno"});
        dataInizioPicker = new JDateChooser();
        dataInizioPicker.setMinSelectableDate(new java.util.Date());
        dataInizioPicker.setDate(new java.util.Date());

        dataFinePicker = new JDateChooser();
        dataFinePicker.setMinSelectableDate(new java.util.Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        dataFinePicker.setDate(cal.getTime());

        // Listener date
        dataInizioPicker.getDateEditor().addPropertyChangeListener("date", evt -> {
            if ("date".equals(evt.getPropertyName()) && dataInizioPicker.getDate() != null) {
                if (dataFinePicker.getDate() == null || dataFinePicker.getDate().before(dataInizioPicker.getDate())) {
                    dataFinePicker.setDate(dataInizioPicker.getDate());
                }
                dataFinePicker.setMinSelectableDate(dataInizioPicker.getDate());
                aggiornaRangeDateStep2();
            }
        });
        dataFinePicker.getDateEditor().addPropertyChangeListener("date", evt -> {
            if ("date".equals(evt.getPropertyName())) {
                aggiornaRangeDateStep2();
            }
        });

        stagioneCombo.addActionListener(e -> aggiornaColturePerStagioneSelezionata());

        panel.add(new JLabel("Lotto:")); panel.add(lottoCombo);
        panel.add(new JLabel("Nome Progetto:")); panel.add(nomeField);
        panel.add(new JLabel("Stagione:")); panel.add(stagioneCombo);
        panel.add(new JLabel("Data Inizio:")); panel.add(dataInizioPicker);
        panel.add(new JLabel("Data Fine:")); panel.add(dataFinePicker);

        return panel;
    }

    private JPanel createStep2PanelPlaceholder() {
        JPanel panel = new JPanel(new BorderLayout());
        step2InnerPanel = new JPanel();
        step2InnerPanel.setLayout(new BoxLayout(step2InnerPanel, BoxLayout.Y_AXIS));
        step2ScrollPane = new JScrollPane(step2InnerPanel);
        panel.add(step2ScrollPane, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private void aggiornaColturePerStagioneSelezionata() {
        String stagione = (String) stagioneCombo.getSelectedItem();
        if (stagione == null) return;

        try {
            colture = controller.getColturePerStagione(stagione);
            aggiornaColturePanel();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore caricamento colture per stagione: " + ex.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
            colture = new ArrayList<>();
            aggiornaColturePanel();
        }
    }

    private void aggiornaColturePanel() {
        step2InnerPanel.removeAll();

        coltureCheckboxes = new ArrayList<>();
        tipoAttivitaPerColtura = new ArrayList<>();
        coltivatoriPerColtura = new ArrayList<>();
        quantitaPerColtura = new ArrayList<>();
        dataPianificataPerColtura = new ArrayList<>();

        if (colture == null || colture.isEmpty()){
            step2InnerPanel.add(new JLabel("Nessuna coltura tipica trovata per questa stagione."));
        } else {
            for (Coltura c : colture) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel emojiLabel = new JLabel(emojiPerColtura.getOrDefault(c.getNome(), "🌱"));
                row.add(emojiLabel);

                JCheckBox chk = new JCheckBox(c.getNome());
                coltureCheckboxes.add(chk);

                JComboBox<String> tipoAtt = new JComboBox<>(new String[]{"semina", "irrigazione", "raccolta"});
                tipoAttivitaPerColtura.add(tipoAtt);

                JComboBox<Coltivatore> colCombo = new JComboBox<>();
                if (coltivatori != null) {
                    for (Coltivatore col : coltivatori) colCombo.addItem(col);
                }
                coltivatoriPerColtura.add(colCombo);

                JTextField quantita = new JTextField(5);
                quantita.setEnabled(false);
                quantitaPerColtura.add(quantita);

                JDateChooser dataPianificata = new JDateChooser();
                dataPianificata.setDateFormatString("yyyy-MM-dd");
                dataPianificata.setEnabled(true);
                dataPianificataPerColtura.add(dataPianificata);

                aggiornaRangeDateSingoloDatePicker(dataPianificata);

                tipoAtt.addActionListener(e -> {
                    boolean isRaccolta = "raccolta".equals(tipoAtt.getSelectedItem());
                    quantita.setEnabled(isRaccolta);
                    if (!isRaccolta) quantita.setText("");
                });

                row.add(chk);
                row.add(new JLabel("Tipo attività:")); row.add(tipoAtt);
                row.add(new JLabel("Coltivatore:")); row.add(colCombo);
                row.add(new JLabel("Q.tà (solo raccolta):")); row.add(quantita);
                row.add(new JLabel("Data Pianificata:")); row.add(dataPianificata);

                step2InnerPanel.add(row);
                step2InnerPanel.add(Box.createVerticalStrut(5));
            }
        }
        step2InnerPanel.revalidate();
        step2InnerPanel.repaint();
        if (step2ScrollPane != null) {
            step2ScrollPane.revalidate();
            step2ScrollPane.repaint();
        }
    }

    private void aggiornaRangeDateStep2() {
        if (dataPianificataPerColtura != null) {
            for (JDateChooser dateChooser : dataPianificataPerColtura) {
                aggiornaRangeDateSingoloDatePicker(dateChooser);
            }
        }
    }
    private void aggiornaRangeDateSingoloDatePicker(JDateChooser dateChooser) {
        java.util.Date dataInizio = dataInizioPicker.getDate();
        java.util.Date dataFine = dataFinePicker.getDate();
        java.util.Date currentDate = dateChooser.getDate();

        if (dataInizio != null) {
            dateChooser.setMinSelectableDate(dataInizio);
            if (currentDate == null || currentDate.before(dataInizio)) {
                dateChooser.setDate(dataInizio);
                currentDate = dataInizio;
            }
        } else {
            dateChooser.setMinSelectableDate(null);
        }

        if (dataFine != null) {
            dateChooser.setMaxSelectableDate(dataFine);
            if (currentDate != null && currentDate.after(dataFine)) {
                dateChooser.setDate(dataFine);
            }
        } else {
            dateChooser.setMaxSelectableDate(null);
        }

        if (dateChooser.getDate() == null && dataInizio != null) {
            dateChooser.setDate(dataInizio);
        }
    }

    // ---------------------- BUTTON PANEL ----------------------
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnBack = new JButton("Indietro");
        btnNext = new JButton("Avanti");
        btnFinish = new JButton("Fine");

        btnBack.addActionListener(e -> {
            cardLayout.previous(cardsPanel);
            updateButtons(0);
        });
        btnNext.addActionListener(e -> {
            if (validaStep1()) {
                cardLayout.next(cardsPanel);
                updateButtons(1);
            }
        });
        btnFinish.addActionListener(e -> salvaProgetto());

        panel.add(btnBack);
        panel.add(btnNext);
        panel.add(btnFinish);

        return panel;
    }

    private boolean validaStep1() {
        if (lottoCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleziona un lotto.", "Errore Step 1", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (nomeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci il nome del progetto.", "Errore Step 1", JOptionPane.WARNING_MESSAGE);
            nomeField.requestFocus();
            return false;
        }

        java.util.Date dInizio = dataInizioPicker.getDate();
        java.util.Date dFine = dataFinePicker.getDate();
        String stagione = (String) stagioneCombo.getSelectedItem(); // Prendiamo la stagione

        if (dInizio == null) {
            JOptionPane.showMessageDialog(this, "Seleziona la data di inizio.", "Errore Step 1", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (dFine == null) {
            JOptionPane.showMessageDialog(this, "Seleziona la data di fine.", "Errore Step 1", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (dInizio.compareTo(dFine) > 0) {
            JOptionPane.showMessageDialog(this, "La data di inizio non può essere successiva alla data di fine.", "Errore Step 1", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!controller.validaDatePerStagione(dInizio, stagione)) {
            JOptionPane.showMessageDialog(this,
                    "La Data di Inizio non è coerente con la stagione selezionata (" + stagione + ").",
                    "Errore Coerenza Date", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!controller.validaDatePerStagione(dFine, stagione)) {
            JOptionPane.showMessageDialog(this,
                    "La Data di Fine non è coerente con la stagione selezionata (" + stagione + ").",
                    "Errore Coerenza Date", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Lotto lottoSel = (Lotto) lottoCombo.getSelectedItem();
            String stagioneSel = (String) stagioneCombo.getSelectedItem();

            if (!controller.isStagioneLibera(lottoSel.getId(), stagioneSel)) {
                JOptionPane.showMessageDialog(this,
                        "Errore: Il lotto '" + lottoSel.getNome() + "' ha già un progetto attivo per la stagione '" + stagioneSel + "'.\n" +
                                "Completa o elimina il progetto esistente prima di crearne uno nuovo.",
                        "Stagione Occupata", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Errore durante la verifica dello stato del lotto: " + ex.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
            return false;
        }


        return true;
    }


    private void updateButtons(int stepIndex) {
        btnBack.setEnabled(stepIndex > 0);
        btnNext.setEnabled(stepIndex < 1);
        btnFinish.setEnabled(stepIndex == 1);
    }

    // ---------------------- FINE ----------------------
    private void salvaProgetto() {
        Lotto lottoSelezionato = (Lotto) lottoCombo.getSelectedItem();
        String nome = nomeField.getText().trim();
        String stagione = (String) stagioneCombo.getSelectedItem();
        Date dataInizio = new Date(dataInizioPicker.getDate().getTime());
        Date dataFine = new Date(dataFinePicker.getDate().getTime());

        boolean almenoUnaSelezionata = coltureCheckboxes != null && coltureCheckboxes.stream().anyMatch(JCheckBox::isSelected);
        if (!almenoUnaSelezionata) {
            JOptionPane.showMessageDialog(this, "Seleziona almeno una coltura nello Step 2.", "Errore Salvataggio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Coltura> coltureSelezionate = new ArrayList<>();
        Map<Coltura, String> attivitaPerColturaMap = new HashMap<>();
        Map<Coltura, Coltivatore> coltivatorePerColturaMap = new HashMap<>();
        Map<Coltura, Double> quantitaPerColturaMap = new HashMap<>();
        Map<Coltura, Date> dataPianificataMap = new HashMap<>();

        if (colture == null || coltureCheckboxes == null || colture.size() != coltureCheckboxes.size() ||
                tipoAttivitaPerColtura == null || colture.size() != tipoAttivitaPerColtura.size() ||
                coltivatoriPerColtura == null || colture.size() != coltivatoriPerColtura.size() ||
                quantitaPerColtura == null || colture.size() != quantitaPerColtura.size() ||
                dataPianificataPerColtura == null || colture.size() != dataPianificataPerColtura.size())
        {
            JOptionPane.showMessageDialog(this, "Errore interno: le liste dei componenti dello Step 2 non sono allineate.", "Errore Critico", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < colture.size(); i++) {
            if (coltureCheckboxes.get(i).isSelected()) {
                Coltura c = colture.get(i);
                coltureSelezionate.add(c);

                String tipo = (String) tipoAttivitaPerColtura.get(i).getSelectedItem();
                Coltivatore col = (Coltivatore) coltivatoriPerColtura.get(i).getSelectedItem();
                java.util.Date dataPianUtil = dataPianificataPerColtura.get(i).getDate();

                if (tipo == null || col == null || dataPianUtil == null) {
                    JOptionPane.showMessageDialog(this, "Completa tutti i campi (tipo, coltivatore, data) per la coltura selezionata: " + c.getNome(), "Errore Salvataggio", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                attivitaPerColturaMap.put(c, tipo);
                coltivatorePerColturaMap.put(c, col);
                dataPianificataMap.put(c, new Date(dataPianUtil.getTime()));

                // Quantità per raccolta
                if ("raccolta".equals(tipo)) {
                    String quantitaStr = quantitaPerColtura.get(i).getText().trim();
                    if (quantitaStr.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Inserisci la quantità prevista per la raccolta di " + c.getNome(), "Errore Salvataggio", JOptionPane.WARNING_MESSAGE);
                        quantitaPerColtura.get(i).requestFocus();
                        return;
                    }
                    try {
                        double q = Double.parseDouble(quantitaStr.replace(',', '.'));
                        if (q < 0) throw new NumberFormatException("Quantità negativa");
                        quantitaPerColturaMap.put(c, q);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Valore quantità non valido per " + c.getNome() + ". Inserisci un numero positivo (es. 10.5).", "Errore Salvataggio", JOptionPane.WARNING_MESSAGE);
                        quantitaPerColtura.get(i).requestFocus();
                        return;
                    }
                } else {
                    quantitaPerColturaMap.put(c, null);
                }
            }
        }

        try {
            controller.salvaNuovoProgetto(
                    nome, stagione, dataInizio, dataFine, lottoSelezionato.getId(),
                    coltureSelezionate, attivitaPerColturaMap, coltivatorePerColturaMap,
                    quantitaPerColturaMap, dataPianificataMap
            );

            JOptionPane.showMessageDialog(this, "Progetto creato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore durante la creazione del progetto nel database: " + e.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore imprevisto durante il salvataggio: " + e.getMessage(), "Errore Applicazione", JOptionPane.ERROR_MESSAGE);
        }
    }

}