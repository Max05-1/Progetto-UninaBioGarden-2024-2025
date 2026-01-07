package main.java.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestisce la connessione al database PostgreSQL di UninaBioGarden.
 * Legge le credenziali da un file config.properties esterno.
 */
public class DatabaseManager {

    private static final Properties properties = new Properties();

    // Blocco statico per caricare le proprietà una sola volta all'avvio
    static {
        try (InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Errore: file config.properties non trovato nel classpath.");
                throw new RuntimeException("File di configurazione non trovato.");
            }
            properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il caricamento del file di configurazione.", e);
        }
    }

    /**
     * Restituisce una connessione al database.
     * @return Connection oggetto connessione
     * @throws SQLException in caso di errore di connessione
     */
    public static Connection getConnection() throws SQLException {
        // Usa le proprietà caricate dal file
        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
        );
    }
}