package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import dao.DatabaseManager;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionTest {

    @Test
    public void testDatabaseConnection() {
        System.out.println("Avvio test di connessione al database...");

        try (Connection conn = DatabaseManager.getConnection()) {
            assertNotNull(conn, "ERRORE: DatabaseManager.getConnection() ha restituito null.");

            assertFalse(conn.isClosed(), "ERRORE: La connessione risulta chiusa.");

            System.out.println("CONNESSO CON SUCCESSO A: " + conn.getMetaData().getURL());
            System.out.println("UTENTE DB: " + conn.getMetaData().getUserName());

        } catch (SQLException e) {
            fail("ECCEZIONE SQL: Impossibile connettersi al database. Dettagli: " + e.getMessage());
        } catch (Exception e) {
            fail("ERRORE GENERICO: " + e.getMessage());
        }
    }
}