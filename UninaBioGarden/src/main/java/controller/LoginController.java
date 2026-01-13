package controller;

import dao.ProprietarioDAO;
import model.Proprietario;
import java.sql.SQLException;

public class LoginController {

    public Proprietario autentica(String username, String password) throws SQLException {
        Proprietario p = ProprietarioDAO.login(username, password); //se c'è nel db ritorna un valore sennò null
        if (p == null) {
            throw new IllegalArgumentException("Username o password errati");
        }
        return p;
    }
}