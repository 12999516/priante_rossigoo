package org.example.persistenza;

import org.example.eccezioni.ErroreDatabaseException;
import org.example.modello.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO {

    private static UtenteDAO instance = null;

    private UtenteDAO(){

    }

    public static UtenteDAO getInstance() {
        if (instance == null){
            instance = new UtenteDAO();
        }
        return instance;
    }

    public void inserisciUtente(String id, String nome, String email, String codiceFiscale, double creadito){

        String sql = "INSERT INTO utenti (id, nome, email, codice_fiscale, credito) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement preparedstatement = conn.prepareStatement(sql)) {
            preparedstatement.setString(1, id);
            preparedstatement.setString(2, nome);
            preparedstatement.setString(3, email);
            preparedstatement.setString(4, codiceFiscale);
            preparedstatement.setDouble(5, creadito);

            preparedstatement.executeUpdate();

        }catch (SQLException e) {
            throw new ErroreDatabaseException("Errore durante l'inserimento dell'utente: " + nome, e);
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
            throw new ErroreDatabaseException("Errore durante l'aggiornamento del credito per l'utente " + id, e);
        }
    }

    public void trovaUtente(String id) {
        String sql = "SELECT * FROM utenti WHERE id = ?";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.execute();
        }catch (SQLException e) {
            throw new ErroreDatabaseException("Errore durante la ricerca dell'utente " + id, e);
        }
    }

    public Utente cercaUtentePerId(String id) {
        String sql = "SELECT * FROM utenti WHERE id = ?";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utente(
                            rs.getString("id"),
                            rs.getString("nome"),
                            rs.getString("email"),
                            rs.getString("codice_fiscale"),
                            rs.getDouble("credito")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Errore durante il recupero dell'utente " + id, e);
        }
    }

    public List<Utente> caricaTuttiUtenti() {
        List<Utente> lista = new ArrayList<>();
        String sql = "SELECT * FROM utenti";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Utente(
                        rs.getString("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("codice_fiscale"),
                        rs.getDouble("credito")
                ));
            }
            return lista;
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Errore durante il caricamento di tutti gli utenti", e);
        }
    }
}
