package dao;

import model.Coltura;
import model.ColturaCatalogo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColturaDAO {
    public static List<Coltura> getAllColture() throws SQLException {
        List<Coltura> colture = new ArrayList<>();
        String query = "SELECT id, nome FROM coltura ORDER BY nome";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Coltura c = new Coltura(
                        rs.getInt("id"),
                        rs.getString("nome")
                );
                colture.add(c);
            }
        }

        return colture;
    }

    public static Coltura getColturaById(int id) throws SQLException {
        String query = "SELECT id, nome FROM coltura WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Coltura(rs.getInt("id"), rs.getString("nome"));
            }
        }
        return null;
    }

    public static List<Coltura> getColturePerStagione(String stagione) throws SQLException {
        List<Coltura> list = new ArrayList<>();
        String sql = "SELECT * FROM get_colture_per_stagione(?::stagione)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stagione);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Coltura(rs.getInt("id"), rs.getString("nome")));
            }
        }
        return list;
    }

    public static List<ColturaCatalogo> getCatalogoCompleto() throws SQLException {
        List<ColturaCatalogo> catalogo = new ArrayList<>();
        String sql = "SELECT * FROM get_catalogo_completo()";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ColturaCatalogo c = new ColturaCatalogo(
                        rs.getString("nome_coltura"),
                        rs.getInt("giorni_maturazione"),
                        rs.getString("stagioni_semina")
                );
                catalogo.add(c);
            }
        }
        return catalogo;
    }
}
