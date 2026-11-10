package org.example.persistenza;

import org.example.eccezioni.ErroreDatabaseException;
import org.example.modello.Bici;
import org.example.modello.ModelloAccess;
import org.example.modello.Monopattino;
import org.example.modello.StatoVeicolo;
import org.example.modello.Veicolo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class VeicoloDAO {

    private static VeicoloDAO instance = null;

    private VeicoloDAO() {
    }

    public static VeicoloDAO getInstance() {
        if (instance == null) {
            instance = new VeicoloDAO();
        }
        return instance;
    }

    public void inserisciVeicolo(Veicolo v) {
        String sql = "INSERT INTO veicoli (codice, tipo, batteria, stato, numero_marce, limitato_pedonale) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String codice = String.valueOf(ModelloAccess.getCodice(v));
            ps.setString(1, codice);

            if (v instanceof Bici b) {
                ps.setString(2, "BICI");
                ps.setDouble(3, ModelloAccess.getLivelloBatteria(v));
                ps.setString(4, ModelloAccess.getStato(v).name());
                ps.setInt(5, ModelloAccess.getNumeroMarcie(b));
                ps.setNull(6, Types.INTEGER);
            } else if (v instanceof Monopattino) {
                ps.setString(2, "MONOPATTINO");
                ps.setDouble(3, ModelloAccess.getLivelloBatteria(v));
                ps.setString(4, ModelloAccess.getStato(v).name());
                ps.setNull(5, Types.INTEGER);
                ps.setInt(6, 0);
            } else {
                ps.setString(2, "GENERICO");
                ps.setDouble(3, ModelloAccess.getLivelloBatteria(v));
                ps.setString(4, ModelloAccess.getStato(v).name());
                ps.setNull(5, Types.INTEGER);
                ps.setNull(6, Types.INTEGER);
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Errore durante l'inserimento del veicolo " + ModelloAccess.getCodice(v), e);
        }
    }

    public void aggiornaStatoEBatteria(String codice, StatoVeicolo stato, int batteria) {
        String sql = "UPDATE veicoli SET stato = ?, batteria = ? WHERE codice = ?";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, stato.name());
            ps.setDouble(2, batteria);
            ps.setString(3, codice);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Errore durante l'aggiornamento del veicolo " + codice, e);
        }
    }

    public Veicolo cercaVeicoloPerCodice(String codice) {
        String sql = "SELECT * FROM veicoli WHERE codice = ?";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codice);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mappaVeicolo(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Errore durante la ricerca del veicolo " + codice, e);
        }
    }

    public List<Veicolo> caricaTuttiVeicoli() {
        List<Veicolo> veicoli = new ArrayList<>();
        String sql = "SELECT * FROM veicoli";

        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Veicolo v = mappaVeicolo(rs);
                if (v != null) {
                    veicoli.add(v);
                }
            }
            return veicoli;
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Errore durante il caricamento di tutti i veicoli", e);
        }
    }

    private Veicolo mappaVeicolo(ResultSet rs) throws SQLException {
        int codice = Integer.parseInt(rs.getString("codice"));
        String tipo = rs.getString("tipo");
        int batteria = (int) Math.round(rs.getDouble("batteria"));
        StatoVeicolo stato = StatoVeicolo.valueOf(rs.getString("stato"));

        if ("BICI".equalsIgnoreCase(tipo)) {
            int marce = rs.getInt("numero_marce");
            if (rs.wasNull()) {
                marce = 5;
            }
            return new Bici(codice, batteria, stato, marce);
        } else if ("MONOPATTINO".equalsIgnoreCase(tipo)) {
            return new Monopattino(codice, batteria, stato, 0.25, 1.0, 60);
        }
        return null;
    }
}
