package controller;

import dao.LottoDAO;
import dao.ProgettoDAO;
import model.Lotto;

import java.sql.SQLException;
import java.util.List;

public class LottoController {

    public List<Lotto> getLottiByProprietario(int idProprietario) throws SQLException {
        return LottoDAO.getLottiByProprietario(idProprietario);
    }

    public boolean isProgettoAttivo(int idLotto) throws SQLException {
        return ProgettoDAO.isProgettoAttivo(idLotto);
    }
}