package controller;

import dao.ColturaDAO;
import dao.LottoDAO;
import dao.ProgettoDAO;
import dao.ColtivatoreDAO;
import model.Attivita;
import model.Coltura;
import model.Progetto;
import model.Lotto;
import model.Coltivatore;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;


public class ProgettoController {
    public ProgettoController() {}

    public boolean aggiornaStatoAttivita( int idAttivita, String nuovoStato ) throws SQLException {
        return ProgettoDAO.aggiornaStatoAttivita(idAttivita, nuovoStato);
    }

    public void eliminaProgetto(int idProgetto) throws SQLException {
        ProgettoDAO.eliminaProgetto(idProgetto);
    }
    public List<Progetto> getProgettiByProprietario( int idProprietario) throws SQLException {
        return ProgettoDAO.getProgettiByProprietario(idProprietario);
    }
    public List<Lotto> getLottiByProprietario( int idProprietario) throws SQLException {
        return LottoDAO.getLottiByProprietario(idProprietario);
    }

    public List<Coltura> getColturePerProgetto (int idProgetto) throws SQLException {
        return ProgettoDAO.getColturePerProgetto(idProgetto);
    }
    public List<Attivita> getAttivitaPerColtura (int idProgetto, int idColtura) throws SQLException {
        return ProgettoDAO.getAttivitaPerColtura(idProgetto, idColtura);
    }

    public List<Attivita> getAttivitaPerProgetto(int idProgetto) throws SQLException {
        return ProgettoDAO.getAttivitaPerProgetto(idProgetto);
    }

    public List<Coltivatore> getAllColtivatori() throws SQLException {
        return ColtivatoreDAO.getAllColtivatori();
    }

    public List<Coltura> getAllColtura() throws SQLException {
        return ColturaDAO.getAllColture();
    }

    public List<Coltura> getColturePerStagione(String stagione) throws SQLException {
        return ColturaDAO.getColturePerStagione(stagione);
    }

    //per coerenza data/staguione
    private boolean validaDatePerStagione ( java.sql.Date data, String stagione ) throws SQLException {
        if (data == null || stagione == null) return false;
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(data);
        int mese = cal.get(java.util.Calendar.MONTH);

        switch (stagione) {
            case "primavera": return mese >= Calendar.MARCH && mese <= Calendar.MAY;
            case "estate": return mese >= Calendar.JUNE && mese <= Calendar.AUGUST;
            case "autunno": return mese >= Calendar.SEPTEMBER && mese <= Calendar.NOVEMBER;
            case "inverno": return mese == Calendar.DECEMBER || mese == Calendar.JANUARY || mese == Calendar.FEBRUARY;
            default: return false;
        }
    }

    //Recupera l'elenco delle colture che possono essere aggiunte a un progetto
    public List<Coltura> getColtureAggiungibili(int idProgetto, String stagione) throws SQLException {
        List<Coltura> tutteColture = this.getColturePerStagione(stagione);

        List<Coltura> coltureAttuali = this.getColturePerProgetto(idProgetto);
        List<Integer> idAttuali = coltureAttuali.stream().map(Coltura::getId).collect(Collectors.toList());

        return tutteColture.stream()
                .filter(c -> !idAttuali.contains(c.getId()))
                .collect(Collectors.toList());
    }
    public void validaEAggiungiColtura(Progetto progetto, Coltura colturaSel, String tipoSel, Coltivatore coltivatoreSel, java.util.Date dataSelUtil, String quantitaStr) throws SQLException, IllegalArgumentException, NumberFormatException {

        if (colturaSel == null || tipoSel == null || coltivatoreSel == null || dataSelUtil == null) {
            throw new IllegalArgumentException("Tutti i campi sono obbligatori.");
        }

        if (tipoSel.equals("semina") && !validaDatePerStagione((Date) dataSelUtil, progetto.getStagione())) {
            throw new IllegalArgumentException("La data selezionata non è coerente con la stagione del progetto (" + progetto.getStagione() + ").");
        }

        Double quantitaSel = null;
        if (tipoSel.equals("raccolta")) {
            try {
                quantitaSel = Double.parseDouble(quantitaStr.trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Quantità non valida. Inserisci un numero.");
            }
        }

        Date dataSelSql = new Date(dataSelUtil.getTime());

        ProgettoDAO.aggiungiColturaAProgetto(
                progetto.getId(),
                colturaSel.getId(),
                tipoSel,
                coltivatoreSel.getId(),
                dataSelSql,
                quantitaSel
        );
    }
}