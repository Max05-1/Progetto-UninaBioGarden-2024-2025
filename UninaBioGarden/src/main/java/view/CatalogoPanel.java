package view;

import controller.CatalogoController;
import model.ColturaCatalogo;
import util.Theme;
import view.components.ColturaCard;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CatalogoPanel extends JPanel {
    //Attributi:
    private CatalogoController controller;
    private JPanel gridPanel;

    //Costruttore:
    public CatalogoPanel() {
        this.controller = new CatalogoController();
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.COLORE_SFONDO_ALT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Titolo:
        JLabel title = new JLabel("Catalogo Colture", SwingConstants.CENTER);
        title.setFont(Theme.FONT_TITOLO_SEZIONE);
        title.setForeground(Theme.COLORE_TESTO_SCURO);
        add(title, BorderLayout.NORTH);

        //Griglia di Card:
        gridPanel = new JPanel();
        int numColonne = calculateNumeroColonne();
        gridPanel.setLayout(new GridLayout(0, numColonne, 15, 15));
        gridPanel.setBackground(Theme.COLORE_SFONDO_ALT);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                ricalcolaLayoutGriglia();
            }
        });

        caricaDatiCatalogo();
    }

    private void caricaDatiCatalogo() {
        gridPanel.removeAll();
        try {
            List<ColturaCatalogo> catalogo = controller.caricaCatalogo();

            if (catalogo.isEmpty()) {
                gridPanel.add(new JLabel("Nessuna coltura trovata nel catalogo."));
            } else {
                for (ColturaCatalogo c : catalogo) {
                    gridPanel.add(new ColturaCard(c));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            gridPanel.add(new JLabel("Errore nel caricamento del catalogo."));
            JOptionPane.showMessageDialog(this,
                    "Errore caricamento catalogo: " + e.getMessage(),
                    "Errore Database", JOptionPane.ERROR_MESSAGE);
        }

        ricalcolaLayoutGriglia();
        revalidate();
        repaint();
    }

    private int calculateNumeroColonne() {
        int panelWidth = getWidth();
        if (panelWidth < 1) panelWidth = 800;
        return Math.max(1, panelWidth / (220 + 15));
    }

    private void ricalcolaLayoutGriglia() {
        int newCols = calculateNumeroColonne();
        LayoutManager layout = gridPanel.getLayout();
        if (layout instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout) layout;
            if (gridLayout.getColumns() != newCols) {
                gridLayout.setColumns(newCols);
                gridPanel.revalidate();
            }
        }
    }
}