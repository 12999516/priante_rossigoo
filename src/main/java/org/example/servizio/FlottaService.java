package org.example.servizio;

import org.example.eccezioni.BatteriaScaricaException;
import org.example.eccezioni.CodiceFiscaleNonValidoException;
import org.example.eccezioni.CreditoInsufficienteException;
import org.example.eccezioni.RossiGoException;
import org.example.eccezioni.UtenteNonTrovatoException;
import org.example.eccezioni.VeicoloNonDisponibileException;
import org.example.modello.ModelloAccess;
import org.example.modello.Noleggio;
import org.example.modello.StatoVeicolo;
import org.example.modello.Utente;
import org.example.modello.Veicolo;
import org.example.persistenza.NoleggioDAO;
import org.example.persistenza.UtenteDAO;
import org.example.persistenza.VeicoloDAO;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FlottaService {

    private static final double COSTO_SBLOCCO = 1.0;
    private static final int SOGLIA_MINIMA_BATTERIA = 15;
    private static final Pattern CF_PATTERN = Pattern.compile("^[A-Z]{6}[0-9LMNPQRSTUV]{2}[A-EHLMPR-T][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z]$", Pattern.CASE_INSENSITIVE);

    private final Map<String, Veicolo> flotta = new HashMap<>();
    private final Map<String, Utente> utenti = new HashMap<>();
    private final Map<String, Noleggio> noleggiAttivi = new HashMap<>();

    public FlottaService() {
        caricaDatiPersistiti();
    }

    private void caricaDatiPersistiti() {
        List<Veicolo> veicoliSalvati = VeicoloDAO.getInstance().caricaTuttiVeicoli();
        for (Veicolo v : veicoliSalvati) {
            flotta.put(String.valueOf(ModelloAccess.getCodice(v)), v);
        }

        List<Utente> utentiSalvati = UtenteDAO.getInstance().caricaTuttiUtenti();
        for (Utente u : utentiSalvati) {
            utenti.put(ModelloAccess.getId(u), u);
        }
    }

    public void registraVeicolo(Veicolo v) {
        if (v == null) {
            throw new RossiGoException("Impossibile registrare un veicolo nullo.");
        }
        String codice = String.valueOf(ModelloAccess.getCodice(v));
        flotta.put(codice, v);
        VeicoloDAO.getInstance().inserisciVeicolo(v);
    }

    public void registraUtente(Utente u) {
        if (u == null) {
            throw new RossiGoException("Impossibile registrare un utente nullo.");
        }

        String cf = ModelloAccess.getCodiceFiscale(u);
        if (cf == null || !CF_PATTERN.matcher(cf.trim()).matches()) {
            throw new CodiceFiscaleNonValidoException("Il codice fiscale fornito non è valido: '" + cf + "'");
        }

        String id = ModelloAccess.getId(u);
        utenti.put(id, u);
        UtenteDAO.getInstance().inserisciUtente(
                id,
                ModelloAccess.getNome(u),
                ModelloAccess.getEmail(u),
                cf,
                ModelloAccess.getCredito(u)
        );
    }

    public void sbloccaVeicolo(String codice, String idUtente) {
        Utente utente = utenti.get(idUtente);
        if (utente == null) {
            throw new UtenteNonTrovatoException("Utente con ID '" + idUtente + "' non trovato nel sistema.");
        }

        Veicolo veicolo = flotta.get(codice);
        if (veicolo == null) {
            throw new VeicoloNonDisponibileException("Veicolo con codice '" + codice + "' non trovato nella flotta.");
        }

        if (ModelloAccess.getStato(veicolo) != StatoVeicolo.LIBERO) {
            throw new VeicoloNonDisponibileException("Il veicolo " + codice + " non è disponibile (stato attuale: " + ModelloAccess.getStato(veicolo) + ").");
        }

        int livelloBatteria = ModelloAccess.getLivelloBatteria(veicolo);
        if (livelloBatteria < SOGLIA_MINIMA_BATTERIA) {
            throw new BatteriaScaricaException("Batteria insufficiente (" + livelloBatteria + "%). Soglia minima richiesta: " + SOGLIA_MINIMA_BATTERIA + "%. Scegli un altro mezzo.");
        }

        double credito = ModelloAccess.getCredito(utente);
        if (credito < COSTO_SBLOCCO) {
            throw new CreditoInsufficienteException("Credito insufficiente (" + String.format("%.2f", credito) + " €) per lo sblocco. Importo minimo richiesto: " + String.format("%.2f", COSTO_SBLOCCO) + " €.");
        }

        ModelloAccess.setStato(veicolo, StatoVeicolo.IN_USO);
        LocalTime now = LocalTime.now();
        Noleggio noleggio = new Noleggio(utente, veicolo, now, null);
        noleggiAttivi.put(codice, noleggio);

        VeicoloDAO.getInstance().aggiornaStatoEBatteria(codice, StatoVeicolo.IN_USO, livelloBatteria);
        NoleggioDAO.getInstance().inserisciNoleggio(idUtente, codice, now);
    }

    public void sbloccaVeicolo(int codice, String idUtente) {
        sbloccaVeicolo(String.valueOf(codice), idUtente);
    }

    public double terminaNoleggio(String codice, int minutiUsati) {
        Noleggio noleggio = noleggiAttivi.remove(codice);
        if (noleggio == null) {
            throw new RossiGoException("Nessun noleggio attivo trovato per il veicolo con codice " + codice + ".");
        }

        Veicolo veicolo = ModelloAccess.getVeicolo(noleggio);
        Utente utente = ModelloAccess.getUtente(noleggio);

        double costoCorsa = ModelloAccess.calcolaCosto(veicolo, minutiUsati);
        double costoTotale = COSTO_SBLOCCO + costoCorsa;

        double consumo = ModelloAccess.consumoBatteriaPerMinuto(veicolo) * minutiUsati;
        int nuovaBatteria = Math.max(0, (int) Math.round(ModelloAccess.getLivelloBatteria(veicolo) - consumo));
        ModelloAccess.setLivelloBatteria(veicolo, nuovaBatteria);

        double nuovoCredito = ModelloAccess.getCredito(utente) - costoTotale;
        ModelloAccess.setCredito(utente, nuovoCredito);

        StatoVeicolo nuovoStato = (nuovaBatteria < SOGLIA_MINIMA_BATTERIA) ? StatoVeicolo.IN_RICARICA : StatoVeicolo.LIBERO;
        ModelloAccess.setStato(veicolo, nuovoStato);

        LocalTime fine = LocalTime.now();
        ModelloAccess.setOrarioFine(noleggio, fine);

        UtenteDAO.getInstance().aggiornaCredito(nuovoCredito, ModelloAccess.getId(utente));
        VeicoloDAO.getInstance().aggiornaStatoEBatteria(codice, nuovoStato, nuovaBatteria);
        NoleggioDAO.getInstance().terminaNoleggio(codice, fine);

        return costoTotale;
    }

    public double terminaNoleggio(int codice, int minutiUsati) {
        return terminaNoleggio(String.valueOf(codice), minutiUsati);
    }

    public void mettiInRicarica(String codice) {
        Veicolo veicolo = flotta.get(codice);
        if (veicolo == null) {
            throw new VeicoloNonDisponibileException("Veicolo " + codice + " non presente nella flotta.");
        }

        ModelloAccess.setStato(veicolo, StatoVeicolo.IN_RICARICA);
        ModelloAccess.setLivelloBatteria(veicolo, 100);
        VeicoloDAO.getInstance().aggiornaStatoEBatteria(codice, StatoVeicolo.IN_RICARICA, 100);
    }

    public void mettiInRicarica(int codice) {
        mettiInRicarica(String.valueOf(codice));
    }

    public double stimaCosto(String codice, int minuti, String idUtente) {
        Utente utente = utenti.get(idUtente);
        if (utente == null) {
            throw new UtenteNonTrovatoException("Utente con ID '" + idUtente + "' non trovato.");
        }

        Veicolo veicolo = flotta.get(codice);
        if (veicolo == null) {
            throw new VeicoloNonDisponibileException("Veicolo con codice '" + codice + "' non trovato.");
        }

        double costoStimato = COSTO_SBLOCCO + ModelloAccess.calcolaCosto(veicolo, minuti);
        double credito = ModelloAccess.getCredito(utente);
        if (credito < costoStimato) {
            throw new CreditoInsufficienteException(
                    "Il credito attuale (" + String.format("%.2f", credito) + " €) non basta per una durata stimata di "
                            + minuti + " minuti (costo previsto: " + String.format("%.2f", costoStimato) + " €)."
            );
        }

        return costoStimato;
    }

    public double stimaCosto(int codice, int minuti, String idUtente) {
        return stimaCosto(String.valueOf(codice), minuti, idUtente);
    }

    public Veicolo getVeicolo(String codice) {
        return flotta.get(codice);
    }

    public Utente getUtente(String id) {
        return utenti.get(id);
    }

    public Map<String, Veicolo> getFlotta() {
        return Collections.unmodifiableMap(flotta);
    }

    public Map<String, Utente> getUtenti() {
        return Collections.unmodifiableMap(utenti);
    }
}
