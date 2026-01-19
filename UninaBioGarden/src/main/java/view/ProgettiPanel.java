package view;

import controller.ProgettoController;
import model.*;
import util.Theme;
import view.components.ProgettoCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProgettiPanel extends JPanel {
    private Proprietario proprietario;
    private JPanel listPanel;
    private JTextField searchField;
    private JButton btnNuovoProgetto;
    private JComboBox<Lotto> lottoFilterCombo; //per filtrare i lotti
    private List<Progetto> tuttiProgetti = Collections.emptyList();
    private List<Lotto> tuttiLotti = Collections.emptyList();

    private ProgettoController controller;

    public ProgettiPanel(Proprietario proprietario) {
        this.proprietario = proprietario;
        this.controller = new ProgettoController();
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.COLORE_SFONDO_ALT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Cerca Progetto:"));
        searchField = new JTextField(15);
        searchField.setToolTipText("Cerca progetto per nome...");
        searchField.addActionListener(e -> filtraProgetti());
        filterPanel.add(searchField);
        filterPanel.add(Box.createHorizontalStrut(15));
        filterPanel.add(new JLabel("Filtra per Lotto:"));
        lottoFilterCombo = new JComboBox<>();
        lottoFilterCombo.addActionListener(e -> filtraProgetti());
        filterPanel.add(lottoFilterCombo);
        filterPanel.add(Box.createHorizontalStrut(15));
        btnNuovoProgetto = new JButton("Nuovo Progetto");
        btnNuovoProgetto.setToolTipText("Crea un nuovo progetto stagionale");
        btnNuovoProgetto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNuovoProgetto.addActionListener(e -> System.out.println("Nuovo progetto stagionale"));
        filterPanel.add(btnNuovoProgetto);
        add(filterPanel, BorderLayout.NORTH);

        listPanel = new JPanel();
        int numColonne = calculateNumeroColonne();
        listPanel.setLayout(new GridLayout(0, numColonne, 15, 15));
        listPanel.setBackground(Theme.COLORE_SFONDO_ALT);
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                ricalcolaLayoutGriglia();
            }
        });
        add(scrollPane, BorderLayout.CENTER);
    }
    private int calculateNumeroColonne() {
        int panelWidth = getWidth();
        if (panelWidth < 1) panelWidth = 800; // Valore di default
        return Math.max(1, panelWidth / (220 + 15));
    }

    private void ricalcolaLayoutGriglia() {
        int newCols = calculateNumeroColonne();
        LayoutManager layout = listPanel.getLayout();
        if (layout instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) layout;
            if (gridLayout.getColumns() != newCols) {
                gridLayout.setColumns(newCols);
                listPanel.revalidate();
            }
        }
    }



    private void caricaDati() {
        try {
            tuttiProgetti = controller.getProgettiByProprietario(proprietario.getId());
            tuttiLotti = controller.getLottiByProprietario(proprietario.getId());

            Object selectedLotto = lottoFilterCombo.getSelectedItem();
            lottoFilterCombo.removeAllItems();
            lottoFilterCombo.addItem(null);
            for (Lotto l : tuttiLotti) {
                lottoFilterCombo.addItem(l);
            }
            if (selectedLotto instanceof Lotto && tuttiLotti.contains(selectedLotto)) {
                lottoFilterCombo.setSelectedItem(selectedLotto);
            } else {
                lottoFilterCombo.setSelectedIndex(0);
            }
            if (Objects.equals(selectedLotto, lottoFilterCombo.getSelectedItem())) {
                filtraProgetti();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore caricamento dati: " + e.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
            tuttiProgetti = Collections.emptyList();
            tuttiLotti = Collections.emptyList();
            lottoFilterCombo.removeAllItems();
            lottoFilterCombo.addItem(null);
            filtraProgetti();
        }
    }
    private void filtraProgetti() {
        listPanel.removeAll();

        if (tuttiProgetti == null || tuttiLotti == null) {
            listPanel.add(new JLabel("Caricamento dati in corso o fallito..."));
            listPanel.revalidate();
            listPanel.repaint();
            return;
        }

        String query = searchField.getText().trim().toLowerCase();
        Lotto lottoSelezionato = (Lotto) lottoFilterCombo.getSelectedItem();

        List<Progetto> filtrati = tuttiProgetti.stream()
                .filter(p -> p.getNome() != null && p.getNome().toLowerCase().contains(query))
                .filter(p -> lottoSelezionato == null || p.getIdLotto() == lottoSelezionato.getId())
                .collect(Collectors.toList());

        if (filtrati.isEmpty()) {
            JLabel emptyLabel = new JLabel("Nessun progetto trovato con i filtri applicati.");
            emptyLabel.setFont(Theme.FONT_TESTO_NORMALE);
            emptyLabel.setForeground(Theme.COLORE_TESTO_GRIGIO);
            listPanel.add(emptyLabel);
        } else {
            for (Progetto p : filtrati) {

                String nomeLotto = p.getNomeLotto();
                if (nomeLotto == null) nomeLotto = "Sconosciuto"; // Fallback

                List<Coltura> colture;
                List<Attivita> attivita;
                try {
                    colture = controller.getColturePerProgetto(p.getId());
                    attivita = controller.getAttivitaPerProgetto(p.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    colture = Collections.emptyList();
                    attivita = Collections.emptyList();
                }

                ProgettoCard card = new ProgettoCard(p, nomeLotto, colture, attivita);
                card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                card.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println(e.getClickCount());
                    }
                });
                listPanel.add(card);
            }
        }

        ricalcolaLayoutGriglia();
        listPanel.revalidate();
        listPanel.repaint();
    }




}
