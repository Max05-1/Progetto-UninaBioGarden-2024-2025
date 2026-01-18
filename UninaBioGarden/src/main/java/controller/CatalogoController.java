package controller;

import dao.ColturaDAO;
import model.ColturaCatalogo;
import java.sql.SQLException;
import java.util.List;

public class CatalogoController {

    public CatalogoController() { }

    //Carica l'elenco completo delle colture per il catalogo:
    public List<ColturaCatalogo> caricaCatalogo() throws SQLException {
        return ColturaDAO.getCatalogoCompleto();
    }
}