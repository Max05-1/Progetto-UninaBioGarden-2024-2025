package dao;

import model.Attivita;
import model.Progetto;
import org.postgresql.util.PGobject;
import model.Coltura;
import model.Coltivatore;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.sql.Date;

public class ProgettoDAO {

    public static List<Progetto> getProgettiByProprietario(int idProprietario) throws SQLException {
        List<Progetto> progetti = new ArrayList<>();

        String query = "SELECT p.id, p.nome, p.stagione, p.data_inizio, p.data_fine, p.id_lotto, p.chiuso, l.nome AS nomeLotto " +
                "FROM progetto p " +
                "JOIN lotto l ON p.id_lotto = l.id " +
                "WHERE l.id_proprietario = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idProprietario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Progetto p = new Progetto();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setStagione(rs.getString("stagione"));
                p.setDataInizio(rs.getDate("data_inizio"));
                p.setDataFine(rs.getDate("data_fine"));
                p.setIdLotto(rs.getInt("id_lotto"));
                p.setChiuso(rs.getBoolean("chiuso"));

                p.setNomeLotto(rs.getString("nomeLotto"));

                progetti.add(p);
            }
        }
        return progetti;
    }


    public static void creaProgettoCompleto(String nome, String stagione, Date dataInizio, Date dataFine,
                                            int idLotto,
                                            List<Coltura> coltureSelezionate,
                                            Map<Coltura, String> attivitaPerColtura,
                                            Map<Coltura, Coltivatore> coltivatorePerColtura,
                                            Map<Coltura, Double> quantitaPerColtura,
                                            Map<Coltura, Date> dataPianificataPerColtura) throws SQLException {

        if (!isStagioneLibera(idLotto, stagione)) {
            throw new SQLException("Impossibile creare il progetto: il lotto selezionato ha già un progetto aperto per questa stagione.");
        }
        String insertProgetto = "INSERT INTO progetto (nome, stagione, data_inizio, data_fine, id_lotto) " +
                "VALUES (?, ?::stagione, ?, ?, ?) RETURNING id";
        String insertColtura = "INSERT INTO progetto_coltura (id_progetto, id_coltura) VALUES (?, ?)";
        String insertAttivita = "INSERT INTO attivita (tipo, stato, data, id_progetto, id_coltura, id_coltivatore, quantita_prevista) " +
                "VALUES (?, 'pianificata', ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psProgetto = conn.prepareStatement(insertProgetto);
                 PreparedStatement psColtura = conn.prepareStatement(insertColtura);
                 PreparedStatement psAttivita = conn.prepareStatement(insertAttivita)) {

                psProgetto.setString(1, nome);
                psProgetto.setString(2, stagione);
                psProgetto.setDate(3, dataInizio);
                psProgetto.setDate(4, dataFine);
                psProgetto.setInt(5, idLotto);

                ResultSet rs = psProgetto.executeQuery();
                if (!rs.next()) throw new SQLException("Errore nella creazione del progetto.");
                int progettoId = rs.getInt(1);

                for (Coltura c : coltureSelezionate) {
                    psColtura.setInt(1, progettoId);
                    psColtura.setInt(2, c.getId());
                    psColtura.executeUpdate();

                    String tipo = attivitaPerColtura.get(c);
                    PGobject pgTipo = new PGobject();
                    pgTipo.setType("attivita_tipo");
                    pgTipo.setValue(tipo);
                    psAttivita.setObject(1, pgTipo);

                    Date dataAttivita = dataPianificataPerColtura.get(c);
                    psAttivita.setDate(2, dataAttivita);
                    psAttivita.setInt(3, progettoId);
                    psAttivita.setInt(4, c.getId());
                    Coltivatore col = coltivatorePerColtura.get(c);
                    psAttivita.setInt(5, col.getId());

                    if ("raccolta".equals(tipo)) {
                        Double q = quantitaPerColtura.get(c);
                        if (q != null) psAttivita.setDouble(6, q);
                        else psAttivita.setNull(6, Types.NUMERIC);
                    } else {
                        psAttivita.setNull(6, Types.NUMERIC);
                    }
                    psAttivita.executeUpdate();
                }
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public static void eliminaProgetto(int idProgetto) throws SQLException {
        String query = "DELETE FROM progetto WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idProgetto);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) throw new SQLException("Nessun progetto trovato con ID " + idProgetto);
        }
    }

    public static List<Coltura> getColturePerProgetto(int idProgetto) throws SQLException {
        List<Coltura> colture = new ArrayList<>();
        String query = "SELECT c.id, c.nome FROM coltura c " +
                "JOIN progetto_coltura pc ON c.id = pc.id_coltura " +
                "WHERE pc.id_progetto = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idProgetto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Coltura c = new Coltura(rs.getInt("id"), rs.getString("nome"));
                colture.add(c);
            }
        }
        return colture;
    }

    public static List<Attivita> getAttivitaPerColtura(int idProgetto, int idColtura) throws SQLException {
        List<Attivita> attivitaList = new ArrayList<>();
        String query = "SELECT id, tipo::text, stato, data, id_progetto, id_coltura, id_coltivatore, quantita_prevista " +
                "FROM attivita WHERE id_progetto = ? AND id_coltura = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idProgetto);
            ps.setInt(2, idColtura);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Attivita a = new Attivita(
                        rs.getInt("id"),
                        rs.getString("tipo"),
                        rs.getString("stato"),
                        rs.getDate("data"),
                        rs.getInt("id_progetto"),
                        rs.getInt("id_coltura"),
                        rs.getInt("id_coltivatore"),
                        rs.getObject("quantita_prevista") != null ? rs.getDouble("quantita_prevista") : null
                );
                attivitaList.add(a);
            }
        }
        return attivitaList;
    }

    public static List<Attivita> getAttivitaPerProgetto(int idProgetto) throws SQLException {
        List<Attivita> attivitaList = new ArrayList<>();
        String query = "SELECT id, tipo::text, stato, data, id_progetto, id_coltura, id_coltivatore, quantita_prevista " +
                "FROM attivita WHERE id_progetto = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idProgetto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Attivita a = new Attivita(
                        rs.getInt("id"),
                        rs.getString("tipo"),
                        rs.getString("stato"),
                        rs.getDate("data"),
                        rs.getInt("id_progetto"),
                        rs.getInt("id_coltura"),
                        rs.getInt("id_coltivatore"),
                        rs.getObject("quantita_prevista") != null ? rs.getDouble("quantita_prevista") : null
                );
                attivitaList.add(a);
            }
        }
        return attivitaList;
    }

    public static boolean aggiornaStatoAttivita(int idAttivita, String nuovoStato) throws SQLException {
        String sqlUpdateAttivita = "UPDATE attivita SET stato = ?::attivita_stato WHERE id = ?";
        String sqlGetProgettoId = "SELECT id_progetto FROM attivita WHERE id = ?";
        String sqlCheckIncomplete = "SELECT 1 FROM attivita WHERE id_progetto = ? AND stato <> 'completata' LIMIT 1";
        String sqlCloseProgetto = "UPDATE progetto SET chiuso = TRUE WHERE id = ?";
        boolean progettoAppenaChiuso = false;
        int idProgetto = -1;
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psUpdateAttivita = conn.prepareStatement(sqlUpdateAttivita)) {
                    PGobject pgStato = new PGobject();
                    pgStato.setType("attivita_stato");
                    pgStato.setValue(nuovoStato);
                    psUpdateAttivita.setObject(1, pgStato);
                    psUpdateAttivita.setInt(2, idAttivita);
                    psUpdateAttivita.executeUpdate();
                }
                if (nuovoStato.equals("completata")) {
                    try (PreparedStatement psGetProgetto = conn.prepareStatement(sqlGetProgettoId)) {
                        psGetProgetto.setInt(1, idAttivita);
                        try (ResultSet rs = psGetProgetto.executeQuery()) {
                            if (rs.next()) {
                                idProgetto = rs.getInt("id_progetto");
                            }
                        }
                    }
                    if (idProgetto != -1) {
                        boolean tutteComplete = false;
                        try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckIncomplete)) {
                            psCheck.setInt(1, idProgetto);
                            try (ResultSet rs = psCheck.executeQuery()) {
                                if (!rs.next()) {
                                    tutteComplete = true;
                                }
                            }
                        }
                        if (tutteComplete) {
                            try (PreparedStatement psClose = conn.prepareStatement(sqlCloseProgetto)) {
                                psClose.setInt(1, idProgetto);
                                psClose.executeUpdate();
                                progettoAppenaChiuso = true;
                            }
                        }
                    }
                }
                conn.commit();
                return progettoAppenaChiuso;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public static void chiudiProgetto(int idProgetto) throws SQLException {
        String sql = "UPDATE progetto SET chiuso = TRUE WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProgetto);
            ps.executeUpdate();
        }
    }

    public static boolean isStagioneLibera(int idLotto, String stagione) throws SQLException {
        String sql = "SELECT 1 FROM progetto WHERE id_lotto = ? AND chiuso = FALSE AND stagione = ?::stagione LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLotto);
            ps.setString(2, stagione);
            ResultSet rs = ps.executeQuery();
            return !rs.next();
        }
    }

    public static void aggiungiColturaAProgetto(int idProgetto, int idColtura, String tipo,
                                                int idColtivatore, Date dataAttivita,
                                                Double quantitaPrevista) throws SQLException {
        String insertColtura = "INSERT INTO progetto_coltura (id_progetto, id_coltura) VALUES (?, ?)";
        String insertAttivita = "INSERT INTO attivita (tipo, stato, data, id_progetto, id_coltura, id_coltivatore, quantita_prevista) " +
                "VALUES (?, 'pianificata', ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psColtura = conn.prepareStatement(insertColtura);
                 PreparedStatement psAttivita = conn.prepareStatement(insertAttivita)) {
                psColtura.setInt(1, idProgetto);
                psColtura.setInt(2, idColtura);
                psColtura.executeUpdate();
                PGobject pgTipo = new PGobject();
                pgTipo.setType("attivita_tipo");
                pgTipo.setValue(tipo);
                psAttivita.setObject(1, pgTipo);
                psAttivita.setDate(2, dataAttivita);
                psAttivita.setInt(3, idProgetto);
                psAttivita.setInt(4, idColtura);
                psAttivita.setInt(5, idColtivatore);
                if ("raccolta".equals(tipo) && quantitaPrevista != null) {
                    psAttivita.setDouble(6, quantitaPrevista);
                } else {
                    psAttivita.setNull(6, Types.NUMERIC);
                }
                psAttivita.executeUpdate();
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}