package dao;

import model.Proprietario;
import java.sql.*;

public class ProprietarioDAO {

    public static Proprietario login(String username, String password) throws SQLException {
        String sql = "SELECT id, username, nome, cognome, email " +
                "FROM proprietario " +
                "WHERE id = verifica_accesso(?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Proprietario(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("email")
                    );
                } else {
                    return null;
                }
            }
        }
    }
}