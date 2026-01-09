// main.java.model/Notifica.java
package model;

import java.util.Date;

public class Notifica {
    private int id;
    private int idProgetto;
    private int idProprietario;
    private Integer idColtivatore; // null = rivolta a tutti
    private String messaggio;
    private Date dataInvio;
    private String tipoNotifica; // 'attivita_imminente' | 'anomalia'

    public Notifica() {}

    public Notifica(int id, int idProgetto, int idProprietario, Integer idColtivatore,
                    String messaggio, Date dataInvio, String tipoNotifica) {
        this.id = id;
        this.idProgetto = idProgetto;
        this.idProprietario = idProprietario;
        this.idColtivatore = idColtivatore;
        this.messaggio = messaggio;
        this.dataInvio = dataInvio;
        this.tipoNotifica = tipoNotifica;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdProgetto() { return idProgetto; }
    public void setIdProgetto(int idProgetto) { this.idProgetto = idProgetto; }

    public int getIdProprietario() { return idProprietario; }
    public void setIdProprietario(int idProprietario) { this.idProprietario = idProprietario; }

    public Integer getIdColtivatore() { return idColtivatore; }
    public void setIdColtivatore(Integer idColtivatore) { this.idColtivatore = idColtivatore; }

    public String getMessaggio() { return messaggio; }
    public void setMessaggio(String messaggio) { this.messaggio = messaggio; }

    public Date getDataInvio() { return dataInvio; }
    public void setDataInvio(Date dataInvio) { this.dataInvio = dataInvio; }

    public String getTipoNotifica() { return tipoNotifica; }
    public void setTipoNotifica(String tipoNotifica) { this.tipoNotifica = tipoNotifica; }
}