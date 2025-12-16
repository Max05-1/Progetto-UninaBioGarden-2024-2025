package model;

public class Coltura {
    //Attributi:
    private int id;
    private String nome;

    //Costruttore:
    public Coltura(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    //Metodi:

    //Getter:
    public int getId() { return id; }
    public String getNome() { return nome; }

    @Override
    public String toString() { return nome; }
}
