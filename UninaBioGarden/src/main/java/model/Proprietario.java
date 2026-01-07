package main.java.model;

public class Proprietario {
    private int id;
    private String username;
    private String nome;
    private String cognome;
    private String email;

    public Proprietario(int id, String username, String nome, String cognome, String email) {
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    // Getter
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return nome + " " + cognome;
    }
}
