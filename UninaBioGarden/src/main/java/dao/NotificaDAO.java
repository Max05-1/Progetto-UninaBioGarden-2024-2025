// dao/NotificaDAO.java
package dao;

import model.Notifica;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificaDAO {

    public static void creaNotifica(Notifica n) throws SQLException {
        String sql = "INSERT INTO notifica (messaggio, data_invio, id_progetto, id_proprietario, id_coltivatore, tipo_notifica) " +
                "VALUES (?, COALESCE(?, CURRENT_TIMESTAMP), ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, n.getMessaggio());
            if (n.getDataInvio() != null) {
                ps.setTimestamp(2, new Timestamp(n.getDataInvio().getTime()));
            } else {
                ps.setNull(2, Types.TIMESTAMP);
            }
            ps.setInt(3, n.getIdProgetto());
            ps.setInt(4, n.getIdProprietario());
            if (n.getIdColtivatore() != null) ps.setInt(5, n.getIdColtivatore());
            else ps.setNull(5, Types.INTEGER);

            PGobject tipo = new PGobject();
            tipo.setType("tipo_notifica");
            tipo.setValue(n.getTipoNotifica());
            ps.setObject(6, tipo);

            ps.executeUpdate();
        }
    }

    public static List<Notifica> getNotificheByProprietario(int idProprietario) throws SQLException {
        String sql = "SELECT id, messaggio, data_invio, id_progetto, id_proprietario, id_coltivatore, tipo_notifica::text " +
                "FROM notifica WHERE id_proprietario = ? ORDER BY data_invio DESC";
        List<Notifica> list = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProprietario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public static List<Notifica> getNotificheByProgetto(int idProgetto) throws SQLException {
        String sql = "SELECT id, messaggio, data_invio, id_progetto, id_proprietario, id_coltivatore, tipo_notifica::text " +
                "FROM notifica WHERE id_progetto = ? ORDER BY data_invio DESC";
        List<Notifica> list = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProgetto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public static void eliminaNotifica(int idNotifica) throws SQLException {
        String sql = "DELETE FROM notifica WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idNotifica);
            ps.executeUpdate();
        }
    }

    private static Notifica mapRow(ResultSet rs) throws SQLException {
        return new Notifica(
                rs.getInt("id"),
                rs.getInt("id_progetto"),
                rs.getInt("id_proprietario"),
                (Integer) rs.getObject("id_coltivatore"),
                rs.getString("messaggio"),
                rs.getTimestamp("data_invio"),
                rs.getString("tipo_notifica")
        );
    }
}