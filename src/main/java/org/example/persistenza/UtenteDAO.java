package org.example.persistenza;

import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.example.eccezioni.ErroreDatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UtenteDAO {

    private static UtenteDAO instance = null;

    private UtenteDAO(){

    }

    public static UtenteDAO getInstance() {
        if (instance == null){
            instance=new UtenteDAO();
        }
        return instance;
    }

    public void inserisciUtente(String id, String nome, String email, String codiceFiscale, double creadito){

        String sql = "INSERT INTO utenti (id, nome, email, codice_fiscale, credito) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement preparedstatement= conn.prepareStatement(sql)) {
            preparedstatement.setString(1, id);
            preparedstatement.setString(2, nome);
            preparedstatement.setString(3, email);
            preparedstatement.setString(4, codiceFiscale);
            preparedstatement.setDouble(5, creadito);

            preparedstatement.executeUpdate();

        }catch (SQLException e) {
            throw new ErroreDatabaseException("errore", e);
        }

    }

    public void aggiornaCredito(double credito, String id){
        String sql = "UPDATE utenti SET credito = ? WHERE id = ?";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, credito);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroreDatabaseException("errore", e);
        }
    }

    public void trovaUtente(String id) {
        String sql = "SELECT * FROM utenti WHERE id = ?";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.execute();
        }catch (SQLException e) {
            throw new ErroreDatabaseException("errore", e);
        }
    }
}
