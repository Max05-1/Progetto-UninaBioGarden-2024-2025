package model;

public class Lotto {
    //Attributi:
    private int id;
    private String nome;
    private double dimensione;
    private int numeroProgetti;

    //Metodi:

    //Getter:
    public int getId() { return id; }
    public String getNome() { return nome; }
    public double getDimensione() { return dimensione; }
    public int getNumeroProgetti() { return numeroProgetti; }

    //Setter:
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDimensione(double dimensione) { this.dimensione = dimensione; }
    public void setNumeroProgetti(int numeroProgetti) { this.numeroProgetti = numeroProgetti; }

    @Override
    public String toString() {
        return nome;
    }
}
