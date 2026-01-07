package main.java.model;

import java.sql.Date;

public class Attivita {
    private int id;
    private String tipo;   // es. "semina", "irrigazione", "raccolta"
    private String stato;  // es. "pianificata", "in corso", "completato"
    private Date data;
    private int idProgetto;
    private int idColtura;
    private int idColtivatore;
    private Double quantitaPrevista;

    public Attivita(int id, String tipo, String stato, Date data, int idProgetto, int idColtura, int idColtivatore, Double quantitaPrevista) {
        this.id = id;
        this.tipo = tipo;
        this.stato = stato;
        this.data = data;
        this.idProgetto = idProgetto;
        this.idColtura = idColtura;
        this.idColtivatore = idColtivatore;
        this.quantitaPrevista = quantitaPrevista;
    }

    // Getter
    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public String getStato() { return stato; }
    public Date getData() { return data; }
    public int getIdProgetto() { return idProgetto; }
    public int getIdColtura() { return idColtura; }
    public int getIdColtivatore() { return idColtivatore; }
    public Double getQuantitaPrevista() { return quantitaPrevista; }

    // Setter
    public void setStato(String stato) { this.stato = stato; }
}