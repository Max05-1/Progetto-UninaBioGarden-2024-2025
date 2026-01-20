package view;

import controller.LottoController;
import model.Lotto;
import model.Proprietario;
import util.Theme;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class LottiPanel extends JPanel {
    private Proprietario proprietario;
    private JPanel listPanel;
    private LottoController controller;

    //costruttore
    public LottiPanel(Proprietario proprietario) {
        this.proprietario = proprietario;
        this.controller = new LottoController();

        setLayout(new BorderLayout(10, 10));
        this.setBackground(Theme.COLORE_SFONDO_ALT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("I Miei Lotti", SwingConstants.CENTER);
        title.setFont(Theme.FONT_TITOLO_SEZIONE);
        title.setForeground(Theme.COLORE_TESTO_SCURO);
        add(title, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Theme.COLORE_SFONDO_ALT);
        listPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
        loadLotti();
    }

    private void loadLotti() {
        listPanel.removeAll();
        try {
            List<Lotto> lotti = controller.getLottiByProprietario(proprietario.getId());

            if (lotti.isEmpty()) {
                JLabel emptyLabel = new JLabel("Nessun lotto trovato.");
                emptyLabel.setFont(Theme.FONT_TESTO_NORMALE);
                emptyLabel.setForeground(Theme.COLORE_TESTO_GRIGIO);
                emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
                listPanel.add(emptyLabel);
            } else {
                for (Lotto l : lotti) {
                    listPanel.add(createLottoRow(l));
                    listPanel.add(Box.createVerticalStrut(10));
                }
            }

            listPanel.revalidate();
            listPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore caricamento lotti: " + e.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createLottoRow(Lotto l) {

        JPanel row = new JPanel(new GridLayout(2, 2, 10, 2));
        row.setBackground(Theme.COLORE_SFONDO_PANEL);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        Border line = BorderFactory.createLineBorder(Theme.COLORE_BORDO_STANDARD, 1);
        Border margin = BorderFactory.createEmptyBorder(10, 15, 10, 15);
        row.setBorder(BorderFactory.createCompoundBorder(line, margin));

        JLabel nomeLabel = new JLabel(l.getNome());
        nomeLabel.setFont(Theme.FONT_TESTO_NORMALE.deriveFont(Font.BOLD));
        nomeLabel.setForeground(Theme.COLORE_TESTO_SCURO);

        JLabel dimLabel = new JLabel(String.format("Dimensione: %.2f mq", l.getDimensione()));
        dimLabel.setFont(Theme.FONT_TESTO_PICCOLO);
        dimLabel.setForeground(Theme.COLORE_TESTO_SCURO);

        String progettiStr = (l.getNumeroProgetti() == 1) ? "1 Progetto" : l.getNumeroProgetti() + " Progetti";
        JLabel numProgettiLabel = new JLabel(progettiStr);
        numProgettiLabel.setFont(Theme.FONT_TESTO_PICCOLO);
        numProgettiLabel.setForeground(Theme.COLORE_TESTO_GRIGIO);
        numProgettiLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(nomeLabel);
        row.add(numProgettiLabel);
        row.add(dimLabel);
        row.add(new JLabel(""));

        /*
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JOptionPane.showMessageDialog(LottiPanel.this,
                        "Hai selezionato il lotto: " + l.getNome() + " (ID: " + l.getId() + ")",
                        "Info Lotto", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        */
        return row;
    }


}
