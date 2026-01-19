package controller;

import dao.ColtivatoreDAO;
import dao.NotificaDAO;
import dao.ProgettoDAO;
import model.Coltivatore;
import model.Notifica;
import model.Progetto;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class NotificheController {

    public List<Progetto> caricaProgetti(int idProprietario) throws SQLException {
        return ProgettoDAO.getProgettiByProprietario(idProprietario);
    }

    public List<Coltivatore> caricaColtivatoriByProgetto(int idProgetto) throws SQLException {
        return ColtivatoreDAO.getColtivatoriByProgetto(idProgetto);
    }

    public void creaNotificaAttivitaImminente(int idProprietario, int idProgetto, Integer idColtivatore,
                                              String messaggio, Date dataInvio) throws SQLException {
        Notifica n = new Notifica();
        n.setIdProprietario(idProprietario);
        n.setIdProgetto(idProgetto);
        n.setIdColtivatore(idColtivatore); // se è null allora è rivolta a tutti
        n.setMessaggio(messaggio);
        n.setDataInvio(dataInvio); // se è null allora è la data di oggi
        n.setTipoNotifica("attivita_imminente");
        NotificaDAO.creaNotifica(n);
    }

    public void creaNotificaAnomalia(int idProprietario, int idProgetto, Integer idColtivatore,
                                     String messaggio, Date dataInvio) throws SQLException {
        Notifica n = new Notifica();
        n.setIdProprietario(idProprietario);
        n.setIdProgetto(idProgetto);
        n.setIdColtivatore(idColtivatore); // se è null allora è rivolta a tutti
        n.setMessaggio(messaggio);
        n.setDataInvio(dataInvio);
        n.setTipoNotifica("anomalia");
        NotificaDAO.creaNotifica(n);
    }

    public List<Notifica> caricaNotifichePerProgetto(int idProgetto) throws SQLException {
        return NotificaDAO.getNotificheByProgetto(idProgetto);
    }

    public List<Coltivatore> getAllColtivatori() throws SQLException {
        return ColtivatoreDAO.getAllColtivatori(); //
    }
}