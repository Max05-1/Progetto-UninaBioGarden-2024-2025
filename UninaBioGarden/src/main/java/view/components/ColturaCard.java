package view.components;

import model.ColturaCatalogo;
import util.EmojiManager;
import util.Theme;

import javax.swing.*;
import java.awt.*;

public class ColturaCard extends JPanel {

    public ColturaCard(ColturaCatalogo coltura) {
        setLayout(new BorderLayout(5, 8));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLORE_BORDO_STANDARD, 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        setBackground(Theme.COLORE_SFONDO_PANEL);

        // Header: Emoji + Nome
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setOpaque(false);

        String emoji = EmojiManager.getInstance().getEmoji(coltura.getNome());
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        headerPanel.add(emojiLabel);

        JLabel titleLabel = new JLabel(coltura.getNome());
        titleLabel.setFont(Theme.FONT_TESTO_NORMALE.deriveFont(Font.BOLD));
        titleLabel.setForeground(Theme.COLORE_TESTO_SCURO);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Info: Giorni e Stagioni
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel giorniLabel = new JLabel("Maturazione: " + coltura.getGiorniMaturazione() + " giorni");
        giorniLabel.setFont(Theme.FONT_TESTO_PICCOLO);
        giorniLabel.setForeground(Theme.COLORE_TESTO_SCURO);
        infoPanel.add(giorniLabel);

        infoPanel.add(Box.createVerticalStrut(5));

        JLabel stagioniLabel = new JLabel("Semina: " + coltura.getStagioniDisponibili());
        stagioniLabel.setFont(Theme.FONT_TESTO_PICCOLO_ITALIC);
        stagioniLabel.setForeground(Theme.COLORE_TESTO_GRIGIO);
        infoPanel.add(stagioniLabel);

        add(infoPanel, BorderLayout.CENTER);
    }
}