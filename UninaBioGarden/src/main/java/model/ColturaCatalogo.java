package model;

public class ColturaCatalogo {
    //Attributi:
    private String nome;
    private int giorniMaturazione;
    private String stagioniDisponibili;

    //Costruttore:
    public ColturaCatalogo(String nome, int giorniMaturazione, String stagioniDisponibili) {
        this.nome = nome;
        this.giorniMaturazione = giorniMaturazione;
        this.stagioniDisponibili = stagioniDisponibili;
    }

    //Metodi:

    // Getter:
    public String getNome() { return nome; }
    public int getGiorniMaturazione() { return giorniMaturazione; }
    public String getStagioniDisponibili() { return stagioniDisponibili; }
}
