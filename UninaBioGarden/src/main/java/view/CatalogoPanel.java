package view;

import controller.CatalogoController;
import model.ColturaCatalogo;
import util.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CatalogoPanel extends JPanel {
    //Attributi:
    private CatalogoController controller;
    private JPanel gridPanel;

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
    }

}
