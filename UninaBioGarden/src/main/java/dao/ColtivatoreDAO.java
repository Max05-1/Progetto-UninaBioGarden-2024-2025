package dao;

import model.Coltivatore;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColtivatoreDAO {
    public static List<Coltivatore> getAllColtivatori() throws SQLException {
        List<Coltivatore> coltivatori = new ArrayList<>();
        String query = "SELECT id, nome, cognome, username, email FROM coltivatore ORDER BY nome";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Coltivatore c = new Coltivatore(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("username"),
                        rs.getString("email")
                );
                coltivatori.add(c);
            }
        }
        return coltivatori;
    }

    public static Coltivatore getColtivatoreById(int id) throws SQLException {
        String query = "SELECT id, nome, cognome, username, email FROM coltivatore WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Coltivatore(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("username"),
                        rs.getString("email")
                );
            }
        }
        return null;
    }

    public static List<Coltivatore> getColtivatoriByProgetto(int idProgetto) throws SQLException {
        String sql = "SELECT DISTINCT c.id, c.nome, c.cognome, c.username, c.email " +
                "FROM coltivatore c " +
                "JOIN attivita a ON a.id_coltivatore = c.id " +
                "WHERE a.id_progetto = ? " +
                "ORDER BY c.cognome, c.nome";
        List<Coltivatore> out = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProgetto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Coltivatore col = new Coltivatore(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("username"),
                        rs.getString("email")
                );
                out.add(col);
            }
        }
        return out;
    }
}
