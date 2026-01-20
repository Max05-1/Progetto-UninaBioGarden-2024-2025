package view.components;

import util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class MaterialPasswordField extends JPasswordField {

    private Color focusColor = Theme.COLORE_PRIMARIO;
    private Color borderColor = Theme.COLORE_BORDO_STANDARD;

    public MaterialPasswordField(int columns) {
        super(columns);
        setFont(Theme.FONT_TESTO_DETTAGLIO);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        setEchoChar('•');

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(focusColor, 2, true),
                        BorderFactory.createEmptyBorder(4, 7, 4, 7)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 1, true),
                        BorderFactory.createEmptyBorder(5, 8, 5, 8)
                ));
            }
        });
    }

    public void setFocusColor(Color focusColor) {
        this.focusColor = focusColor;
        if (hasFocus()) {
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(focusColor, 2, true),
                    BorderFactory.createEmptyBorder(4, 7, 4, 7)
            ));
        }
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        if (!hasFocus()) {
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1, true),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
        }
    }
}
