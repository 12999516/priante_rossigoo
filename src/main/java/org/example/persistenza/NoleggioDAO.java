package org.example.persistenza;

import org.example.eccezioni.ErroreDatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;

public class NoleggioDAO {

    private static NoleggioDAO instance = null;

    private NoleggioDAO() {
    }

    public static NoleggioDAO getInstance() {
        if (instance == null) {
            instance = new NoleggioDAO();
        }
        return instance;
    }

    public void inserisciNoleggio(String utenteId, String veicoloCodice, LocalTime orarioInizio) {
        String sql = "INSERT INTO noleggi (utente_id, veicolo_codice, orario_inizio) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, utenteId);
            ps.setString(2, veicoloCodice);
            ps.setString(3, orarioInizio != null ? orarioInizio.toString() : LocalTime.now().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Errore durante la registrazione dell'inizio del noleggio", e);
        }
    }

    public void terminaNoleggio(String veicoloCodice, LocalTime orarioFine) {
        String sql = "UPDATE noleggi SET orario_fine = ? WHERE veicolo_codice = ? AND orario_fine IS NULL";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, orarioFine != null ? orarioFine.toString() : LocalTime.now().toString());
            ps.setString(2, veicoloCodice);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Errore durante la registrazione del termine del noleggio per il veicolo " + veicoloCodice, e);
        }
    }
}
