package model;
import java.sql.Date;

public class Progetto {
    private int id;
    private String nome;
    private String stagione;
    private Date dataInizio;
    private Date dataFine;
    private int idLotto;
    private boolean chiuso;

    private String nomeLotto;

    //Costruttore Vuoto
    public Progetto() {}

    //Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getStagione() { return stagione; }
    public Date getDataInizio() { return dataInizio; }
    public Date getDataFine() { return dataFine; }
    public int getIdLotto() { return idLotto; }
    public String getNomeLotto() { return nomeLotto; }
    public String getStato() { return chiuso ? "Chiuso" : "Attivo"; }
    public boolean isChiuso() { return chiuso; }

    //Setters
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setStagione(String stagione) { this.stagione = stagione; }
    public void setDataInizio(Date dataInizio) { this.dataInizio = dataInizio; }
    public void setDataFine(Date dataFine) { this.dataFine = dataFine; }
    public void setIdLotto(int idLotto) { this.idLotto = idLotto; }
    public void setChiuso(boolean chiuso) { this.chiuso = chiuso; }
    public void setNomeLotto(String nomeLotto) { this.nomeLotto = nomeLotto; }

    @Override
    public String toString() {
        return nome;
    }
}