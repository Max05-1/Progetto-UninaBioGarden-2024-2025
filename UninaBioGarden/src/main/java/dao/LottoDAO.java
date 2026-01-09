package dao;

import model.Lotto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LottoDAO {

    public static List<Lotto> getLottiByProprietario(int proprietarioId) throws SQLException {
        List<Lotto> lotti = new ArrayList<>();

        String query = "SELECT l.id, l.nome, l.dimensione_mq, COUNT(p.id) AS numero_progetti " +
                "FROM lotto l " +
                "LEFT JOIN progetto p ON l.id = p.id_lotto " +
                "WHERE l.id_proprietario = ? " +
                "GROUP BY l.id, l.nome, l.dimensione_mq " +
                "ORDER BY l.nome";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, proprietarioId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Lotto lotto = new Lotto();
                lotto.setId(rs.getInt("id"));
                lotto.setNome(rs.getString("nome"));
                lotto.setDimensione(rs.getDouble("dimensione_mq"));

                lotto.setNumeroProgetti(rs.getInt("numero_progetti"));

                lotti.add(lotto);
                System.out.println("DEBUG: Lotto trovato -> " + lotto.getNome() + " ID=" + lotto.getId());
            }
        }

        System.out.println("DEBUG: Totale lotti trovati = " + lotti.size());
        return lotti;
    }
}
