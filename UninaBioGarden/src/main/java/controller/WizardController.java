package controller;

import dao.ColturaDAO;
import dao.ColtivatoreDAO;
import dao.LottoDAO;
import dao.ProgettoDAO;
import model.Coltura;
import model.Coltivatore;
import model.Lotto;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

public class WizardController {

    public WizardController() { }

    public List<Lotto> getLottiDisponibili(int idProprietario) throws SQLException {
        return LottoDAO.getLottiByProprietario(idProprietario);
    }

    public List<Coltura> getAllColture() throws SQLException {
        return ColturaDAO.getAllColture();
    }

    public List<Coltivatore> getAllColtivatori() throws SQLException {
        return ColtivatoreDAO.getAllColtivatori();
    }

    public List<Coltura> getColturePerStagione(String stagione) throws SQLException {
        return ColturaDAO.getColturePerStagione(stagione);
    }

    public boolean isStagioneLibera(int idLotto, String stagione) throws SQLException {
        return ProgettoDAO.isStagioneLibera(idLotto, stagione);
    }

    public void salvaNuovoProgetto(String nome, String stagione, Date dataInizio, Date dataFine,
                                   int idLotto,
                                   List<Coltura> coltureSelezionate,
                                   Map<Coltura, String> attivitaPerColtura,
                                   Map<Coltura, Coltivatore> coltivatorePerColtura,
                                   Map<Coltura, Double> quantitaPerColtura,
                                   Map<Coltura, Date> dataPianificataPerColtura) throws SQLException {

        ProgettoDAO.creaProgettoCompleto(
                nome, stagione, dataInizio, dataFine, idLotto,
                coltureSelezionate, attivitaPerColtura, coltivatorePerColtura,
                quantitaPerColtura, dataPianificataPerColtura
        );
    }

    public boolean validaDatePerStagione(java.util.Date data, String stagione) {
        if (data == null || stagione == null) return false;

        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        int mese = cal.get(Calendar.MONTH);

        switch (stagione) {
            case "primavera":
                // Primavera (Marzo, Aprile, Maggio)
                return mese >= Calendar.MARCH && mese <= Calendar.MAY;
            case "estate":
                // Estate (Giugno, Luglio, Agosto)
                return mese >= Calendar.JUNE && mese <= Calendar.AUGUST;
            case "autunno":
                // Autunno (Settembre, Ottobre, Novembre)
                return mese >= Calendar.SEPTEMBER && mese <= Calendar.NOVEMBER;
            case "inverno":
                // Inverno (Dicembre, Gennaio, Febbraio)
                return mese == Calendar.DECEMBER || mese == Calendar.JANUARY || mese == Calendar.FEBRUARY;
            default:
                return false;
        }
    }
}