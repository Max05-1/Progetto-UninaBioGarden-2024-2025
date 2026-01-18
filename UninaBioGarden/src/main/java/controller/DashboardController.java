package controller;

import dao.ReportDAO;
import dao.LottoDAO;
import model.Lotto;
import model.ReportRaccolta;

import java.sql.SQLException;
import java.util.List;

public class DashboardController {

    private int idProprietario;

    public DashboardController(int idProprietario) {
        this.idProprietario = idProprietario;
    }

    //Carica i lotti appartenenti al proprietario:
    public List<Lotto> getLottiProprietario() throws SQLException {
        return LottoDAO.getLottiByProprietario(idProprietario);
    }

    //Carica i dati del report per un lotto specifico:
    public List<ReportRaccolta> getReportPerLotto(int idLotto) throws SQLException {
        return ReportDAO.getReportPerLotto(idLotto);
    }
}
