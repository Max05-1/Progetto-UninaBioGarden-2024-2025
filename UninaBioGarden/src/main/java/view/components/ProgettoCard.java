package view.components;

import model.Coltura;
import model.Progetto;
import model.Attivita;
import util.EmojiManager;
import util.Theme;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProgettoCard extends JPanel {

    public ProgettoCard(Progetto progetto, String nomeLotto, List<Coltura> colture, List<Attivita> attivita) {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLORE_BORDO_STANDARD, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        setBackground(Theme.COLORE_SFONDO_PANEL);

        int cardWidth = 220;
        setMinimumSize(new Dimension(cardWidth, 120));
        setMaximumSize(new Dimension(cardWidth, Integer.MAX_VALUE));

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText("Clicca per vedere i dettagli del progetto: " + progetto.getNome());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel(progetto.getNome());
        titleLabel.setFont(Theme.FONT_TESTO_NORMALE.deriveFont(Font.BOLD));
        titleLabel.setForeground(Theme.COLORE_TESTO_SCURO);
        titleLabel.setToolTipText(progetto.getNome());
        JLabel statoLabel = new JLabel(progetto.getStato()); //
        statoLabel.setFont(Theme.FONT_TESTO_PICCOLO_ITALIC);
        statoLabel.setForeground(progetto.isChiuso() ? Theme.COLORE_TESTO_GRIGIO : Theme.COLORE_SUCCESSO); //
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statoLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);


        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        JLabel lottoLabel = new JLabel("Lotto: " + nomeLotto);
        lottoLabel.setFont(Theme.FONT_TESTO_PICCOLO);
        lottoLabel.setForeground(Theme.COLORE_TESTO_SCURO);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String dataInizioStr = progetto.getDataInizio() != null ? dateFormat.format(progetto.getDataInizio()) : "N/D";
        String dataFineStr = progetto.getDataFine() != null ? dateFormat.format(progetto.getDataFine()) : "N/D";
        JLabel dateLabel = new JLabel("Periodo: " + dataInizioStr + " - " + dataFineStr);
        dateLabel.setFont(Theme.FONT_TESTO_PICCOLO);
        dateLabel.setForeground(Theme.COLORE_TESTO_GRIGIO);
        infoPanel.add(lottoLabel);
        infoPanel.add(dateLabel);
        add(infoPanel, BorderLayout.CENTER);

        JPanel attivitaPanel = new JPanel();
        attivitaPanel.setLayout(new BoxLayout(attivitaPanel, BoxLayout.Y_AXIS));
        attivitaPanel.setOpaque(false);
        attivitaPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        if (attivita != null && !attivita.isEmpty()) {
            EmojiManager emojiManager = EmojiManager.getInstance();

            Map<Integer, String> colturaIdToNameMap = colture.stream()
                    .collect(Collectors.toMap(Coltura::getId, Coltura::getNome));

            for (Attivita a : attivita) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
                row.setOpaque(false);

                String nomeColtura = colturaIdToNameMap.getOrDefault(a.getIdColtura(), "Sconosciuta");
                String emoji = emojiManager.getEmoji(nomeColtura); //

                JLabel emojiLabel = new JLabel(emoji);
                emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                emojiLabel.setToolTipText(nomeColtura + " (" + a.getTipo() + ")");

                String tipoAttivita = a.getTipo();
                JLabel statoAttivitaLabel = new JLabel(": " + tipoAttivita + " - " + a.getStato());

                statoAttivitaLabel.setFont(Theme.FONT_TESTO_PICCOLO);

                switch (a.getStato()) {
                    case "completata":
                        statoAttivitaLabel.setForeground(Theme.COLORE_SUCCESSO);
                        break;
                    case "in corso":
                        statoAttivitaLabel.setForeground(Theme.COLORE_PRIMARIO);
                        break;
                    case "pianificata":
                    default:
                        statoAttivitaLabel.setForeground(Theme.COLORE_TESTO_GRIGIO);
                        break;
                }

                row.add(emojiLabel);
                row.add(statoAttivitaLabel);
                attivitaPanel.add(row);
            }
        } else {
            JLabel noAttivitaLabel = new JLabel("Nessuna attività pianificata");
            noAttivitaLabel.setFont(Theme.FONT_TESTO_PICCOLO_ITALIC);
            noAttivitaLabel.setForeground(Theme.COLORE_TESTO_GRIGIO);
            attivitaPanel.add(noAttivitaLabel);
        }

        add(attivitaPanel, BorderLayout.SOUTH);
    }
}