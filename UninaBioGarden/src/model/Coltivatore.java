package model;

public class Coltivatore {
    //Attributi:
    private int id;
    private String nome;
    private String cognome;
    private String username;
    private String email;

    //Costruttore:
    public Coltivatore(int id, String nome, String cognome, String username, String email) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.email = email;
    }

    //Metodi:

    //Getter:
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }



}
