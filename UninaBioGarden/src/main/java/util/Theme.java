package util;

import java.awt.Color;
import java.awt.Font;

public class Theme {

    // --- Colori Principali ---
    /** Colore primario usato per elementi in focus, titoli importanti (Arancione). */
    public static final Color COLORE_PRIMARIO = new Color(198, 107, 52);

    /** Colore per il testo standard, sottotitoli (Verde scuro). */
    public static final Color COLORE_TESTO_SCURO = new Color(61, 70, 39);

    /** Colore per testo meno importante o stati (Grigio). */
    public static final Color COLORE_TESTO_GRIGIO = Color.GRAY;

    /** Colore per testo/elementi di enfasi negativa (Rosso). */
    public static final Color COLORE_ERRORE = Color.RED;

    /** Colore per testo/elementi di enfasi positiva (Verde). */
    public static final Color COLORE_SUCCESSO = new Color(0, 128, 0);


    // --- Colori Sfondo ---
    /** Sfondo principale per aree di contenuto (Bianco). */
    public static final Color COLORE_SFONDO_PANEL = Color.WHITE;

    /** Sfondo per aree secondarie o menu laterale (Beige chiaro). */
    public static final Color COLORE_SFONDO_MENU = new Color(246, 242, 230);

    /** Sfondo leggermente diverso per alternanza o enfasi (Beige chiarissimo). */
    public static final Color COLORE_SFONDO_ALT = new Color(255, 253, 242);

    /** Sfondo per card o elementi in risalto (Grigio chiarissimo). */
    public static final Color COLORE_SFONDO_CARD = new Color(245, 245, 245);


    // --- Colori Bordi ---
    /** Colore standard per bordi (Grigio). */
    public static final Color COLORE_BORDO_STANDARD = Color.GRAY;


    // --- Font ---
    private static final String FONT_FAMILY_PRINCIPALE = "SansSerif"; // O "Arial" se preferisci
    // private static final String FONT_FAMILY_PRINCIPALE = "Arial";

    /** Font grande e bold per titoli principali. */
    public static final Font FONT_TITOLO_PRINCIPALE = new Font(FONT_FAMILY_PRINCIPALE, Font.BOLD, 32);

    /** Font medio per titoli di sezione o card. */
    public static final Font FONT_TITOLO_SEZIONE = new Font(FONT_FAMILY_PRINCIPALE, Font.BOLD, 24);

    /** Font medio-piccolo per sottotitoli o testo normale. */
    public static final Font FONT_TESTO_NORMALE = new Font(FONT_FAMILY_PRINCIPALE, Font.PLAIN, 16);

    /** Font piccolo per dettagli, email, date. */
    public static final Font FONT_TESTO_DETTAGLIO = new Font(FONT_FAMILY_PRINCIPALE, Font.PLAIN, 14);

    /** Font più piccolo per note o stati. */
    public static final Font FONT_TESTO_PICCOLO = new Font(FONT_FAMILY_PRINCIPALE, Font.PLAIN, 12);

    /** Font piccolo corsivo per stati o info secondarie. */
    public static final Font FONT_TESTO_PICCOLO_ITALIC = new Font(FONT_FAMILY_PRINCIPALE, Font.ITALIC, 12);


    // Costruttore privato per impedire istanziazione
    private Theme() {}
}