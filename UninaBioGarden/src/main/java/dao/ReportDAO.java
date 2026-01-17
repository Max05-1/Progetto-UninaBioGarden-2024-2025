package dao;

import model.ReportRaccolta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public static List<ReportRaccolta> getReportPerLotto(int idLotto) throws SQLException {
        List<ReportRaccolta> reportList = new ArrayList<>();
        String sql = "SELECT * FROM report_raccolte_lotto(?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idLotto);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long numeroRaccolte = rs.getLong("numero_raccolte");

                    Double media = rs.getObject("quantita_media") != null ? rs.getDouble("quantita_media") : null;
                    Double minima = rs.getObject("quantita_minima") != null ? rs.getDouble("quantita_minima") : null;
                    Double massima = rs.getObject("quantita_massima") != null ? rs.getDouble("quantita_massima") : null;

                    ReportRaccolta r = new ReportRaccolta(
                            rs.getInt("id_coltura"),
                            rs.getString("nome_coltura"),
                            numeroRaccolte,
                            media,
                            minima,
                            massima
                    );
                    reportList.add(r);
                }
            }
        }

        return reportList;
    }
}