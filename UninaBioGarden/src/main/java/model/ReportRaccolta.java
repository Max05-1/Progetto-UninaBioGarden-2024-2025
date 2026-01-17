package model;

public class ReportRaccolta {
    //Attributi:
    private int idColtura;
    private String nomeColtura;
    private long numeroRaccolte;
    private double quantitaMedia;
    private double quantitaMinima;
    private double quantitaMassima;

    //Costruttore:
    public ReportRaccolta(int idColtura, String nomeColtura, long numeroRaccolte,
                          double quantitaMedia, double quantitaMinima, double quantitaMassima) {
        this.idColtura = idColtura;
        this.nomeColtura = nomeColtura;
        this.numeroRaccolte = numeroRaccolte;
        this.quantitaMedia = quantitaMedia;
        this.quantitaMinima = quantitaMinima;
        this.quantitaMassima = quantitaMassima;
    }

    //Metodi:

    //Getter:
    public int getIdColtura() { return idColtura; }
    public String getNomeColtura() { return nomeColtura; }
    public long getNumeroRaccolte() { return numeroRaccolte; }
    public double getQuantitaMedia() { return quantitaMedia; }
    public double getQuantitaMinima() { return quantitaMinima; }
    public double getQuantitaMassima() { return quantitaMassima; }
}