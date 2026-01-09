package dao;

import model.Attivita;
import model.Coltivatore;
import org.postgresql.util.PGobject; // <-- IMPORT AGGIUNTO

import java.sql.*;
import java.util.List;

public class AttivitaDAO {


    public static void creaAttivita(int idProgetto, int idColtura, String tipo,
                                    List<Coltivatore> coltivatori, Double quantitaPrevista) throws SQLException {

        String insertAttivita = "INSERT INTO attivita (tipo, stato, data, id_progetto, id_coltura, id_coltivatore, quantita_prevista) " +
                "VALUES (?, 'pianificata', CURRENT_DATE, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(insertAttivita)) {

                PGobject pgTipo = new PGobject();
                pgTipo.setType("attivita_tipo");
                pgTipo.setValue(tipo);
                ps.setObject(1, pgTipo);

                ps.setInt(2, idProgetto);
                ps.setInt(3, idColtura);

                if (quantitaPrevista != null) {
                    ps.setDouble(5, quantitaPrevista);
                } else {
                    ps.setNull(5, Types.NUMERIC);
                }

                for (Coltivatore c : coltivatori) {
                    ps.setInt(4, c.getId());
                    ps.addBatch();
                }

                ps.executeBatch();

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}