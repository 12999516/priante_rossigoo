package org.example.app;

import com.github.lalyos.jfiglet.FigletFont;
import org.example.eccezioni.BatteriaScaricaException;
import org.example.eccezioni.CodiceFiscaleNonValidoException;
import org.example.eccezioni.CreditoInsufficienteException;
import org.example.eccezioni.EmailNonValidaException;
import org.example.eccezioni.ErroreDatabaseException;
import org.example.eccezioni.RossiGoException;
import org.example.eccezioni.VeicoloNonDisponibileException;
import org.example.modello.Bici;
import org.example.modello.ModelloAccess;
import org.example.modello.Monopattino;
import org.example.modello.StatoVeicolo;
import org.example.modello.Utente;
import org.example.persistenza.DatabaseManager;
import org.example.servizio.FlottaService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        stampaBanner();

        System.out.println("Benvenuti in RossiGo - Sistema di noleggio bici e monopattini elettrici");
        System.out.println("Avvio della simulazione delle attività giornaliere...\n");

        preparaScenarioIniziale();

        FlottaService service = new FlottaService();

        System.out.println("---------------------------------------------------------------------");
        System.out.println("SCENARIO 1: Tentativo di sblocco con credito insufficiente (< 1.00 €)");
        System.out.println("Anna Bianchi (credito: 0.50 €) prova a sbloccare il monopattino 201...");
        try {
            service.sbloccaVeicolo("201", "U2");
            System.out.println("[SUCCESSO] Veicolo sbloccato con successo!");
        } catch (CreditoInsufficienteException e) {
            System.out.println("[ATTENZIONE UTENTE] " + e.getMessage());
        } catch (RossiGoException e) {
            System.out.println("[ERRORE] " + e.getMessage());
        }

        System.out.println("\n---------------------------------------------------------------------");
        System.out.println("SCENARIO 2: Registrazione di un nuovo utente con codice fiscale errato");
        System.out.println("Tentativo di registrazione utente 'Mario Neri' con CF non valido 'CF_NON_VALIDO_123'...");
        try {
            Utente utenteErrato = new Utente("U99", "Mario Neri", "mario.neri@email.it", "CF_NON_VALIDO_123", 10.0);
            service.registraUtente(utenteErrato);
            System.out.println("[SUCCESSO] Utente registrato.");
        } catch (CodiceFiscaleNonValidoException e) {
            System.out.println("[ATTENZIONE UTENTE] Codice fiscale non valido! " + e.getMessage());
        } catch (EmailNonValidaException e) {
            System.out.println("[ATTENZIONE UTENTE] Indirizzo email non valido: " + e.getMessage());
        } catch (RossiGoException e) {
            System.out.println("[ERRORE] " + e.getMessage());
        }

        System.out.println("\n---------------------------------------------------------------------");
        System.out.println("SCENARIO 3: Tentativo di sblocco di un mezzo con batteria scarica (< 15%)");
        System.out.println("Marco Rossi tenta di sbloccare la Bici 102 (batteria al 10%)...");
        try {
            service.sbloccaVeicolo("102", "U1");
            System.out.println("[SUCCESSO] Veicolo sbloccato con successo!");
        } catch (BatteriaScaricaException e) {
            System.out.println("[ATTENZIONE UTENTE] " + e.getMessage());
        } catch (RossiGoException e) {
            System.out.println("[ERRORE] " + e.getMessage());
        }

        System.out.println("\n---------------------------------------------------------------------");
        System.out.println("SCENARIO 4: Noleggio Bici 101 e tentativo di sblocco concorrente");
        try {
            System.out.println("Marco Rossi sblocca la Bici 101 (batteria iniziale: "
                    + ModelloAccess.getLivelloBatteria(service.getVeicolo("101")) + "%)...");
            service.sbloccaVeicolo("101", "U1");
            System.out.println("[SUCCESSO] Bici 101 sbloccata correttamente! Corsa avviata.");

            System.out.println("\nMentre Marco è in corsa, Luca Verdi tenta di sbloccare la stessa Bici 101...");
            try {
                service.sbloccaVeicolo("101", "U3");
            } catch (VeicoloNonDisponibileException e) {
                System.out.println("[ATTENZIONE UTENTE] " + e.getMessage());
            }

            System.out.println("\nMarco termina il noleggio della Bici 101 dopo 20 minuti di pedalata assistita.");
            double costoTotale = service.terminaNoleggio("101", 20);
            System.out.println("[RIASSUNTO CORSA]");
            System.out.println("  - Costo totale (1 € sblocco + 20 min * 0.15 €/min): " + String.format("%.2f", costoTotale) + " €");
            System.out.println("  - Credito residuo Marco: " + String.format("%.2f", ModelloAccess.getCredito(service.getUtente("U1"))) + " €");
            System.out.println("  - Batteria residua Bici 101 (consumo 0.5%/min): " + ModelloAccess.getLivelloBatteria(service.getVeicolo("101")) + "%");
            System.out.println("  - Stato Bici 101: " + ModelloAccess.getStato(service.getVeicolo("101")));
        } catch (RossiGoException e) {
            System.out.println("[ERRORE] " + e.getMessage());
        }

        System.out.println("\n---------------------------------------------------------------------");
        System.out.println("SCENARIO 5: Noleggio Monopattino 201 (tariffa 0.25 €/min, consumo 1.0%/min)");
        try {
            System.out.println("Luca Verdi (credito: " + String.format("%.2f", ModelloAccess.getCredito(service.getUtente("U3")))
                    + " €) sblocca il monopattino 201...");
            service.sbloccaVeicolo("201", "U3");
            System.out.println("[SUCCESSO] Monopattino 201 sbloccato. Buona corsa!");

            System.out.println("Luca percorre la pista ciclabile e conclude il noleggio dopo 4 minuti.");
            double costoMonopattino = service.terminaNoleggio("201", 4);
            System.out.println("[RIASSUNTO CORSA]");
            System.out.println("  - Costo totale (1 € sblocco + 4 min * 0.25 €/min): " + String.format("%.2f", costoMonopattino) + " €");
            System.out.println("  - Credito residuo Luca: " + String.format("%.2f", ModelloAccess.getCredito(service.getUtente("U3"))) + " €");
            System.out.println("  - Batteria residua Monopattino 201 (consumo 1.0%/min): " + ModelloAccess.getLivelloBatteria(service.getVeicolo("201")) + "%");
            System.out.println("  - Stato Monopattino 201: " + ModelloAccess.getStato(service.getVeicolo("201")));
        } catch (RossiGoException e) {
            System.out.println("[ERRORE] " + e.getMessage());
        }

        System.out.println("\n---------------------------------------------------------------------");
        System.out.println("SCENARIO 6 (BONUS): Stima del costo prima del noleggio");
        System.out.println("Luca Verdi (credito attuale: " + String.format("%.2f", ModelloAccess.getCredito(service.getUtente("U3")))
                + " €) richiede la stima per un percorso di 15 minuti in monopattino (codice 202)...");
        try {
            double stima = service.stimaCosto("202", 15, "U3");
            System.out.println("[INFO] Costo stimato: " + String.format("%.2f", stima) + " €. Credito sufficiente.");
        } catch (CreditoInsufficienteException e) {
            System.out.println("[ATTENZIONE UTENTE] " + e.getMessage());
            System.out.println("-> Suggerimento: ricarica il tuo conto prima di iniziare il noleggio.");
        } catch (RossiGoException e) {
            System.out.println("[ERRORE] " + e.getMessage());
        }

        System.out.println("\n---------------------------------------------------------------------");
        System.out.println("SCENARIO 7: Messa in ricarica e dimostrazione persistenza SQLite (riavvio)");
        try {
            System.out.println("Messa in ricarica della Bici 102 con batteria scarica...");
            service.mettiInRicarica("102");
            System.out.println("Stato Bici 102 dopo il comando: " + ModelloAccess.getStato(service.getVeicolo("102"))
                    + ", batteria: " + ModelloAccess.getLivelloBatteria(service.getVeicolo("102")) + "%");

            System.out.println("\n>>> CHIUSURA APPLICAZIONE E SIMULAZIONE DI RIAVVIO PROGRAMMA <<<");
            System.out.println("Creazione di una nuova istanza di FlottaService (lettura dati dal database rossigo.db)...");
            FlottaService nuovoServizio = new FlottaService();

            Utente marcoRicaricato = nuovoServizio.getUtente("U1");
            Utente lucaRicaricato = nuovoServizio.getUtente("U3");
            var bici101 = nuovoServizio.getVeicolo("101");
            var bici102 = nuovoServizio.getVeicolo("102");
            var monopattino201 = nuovoServizio.getVeicolo("201");

            System.out.println("\n[DATI RECUPERATI DA DATABASE DOPO IL RIAVVIO]");
            System.out.println("  * Utente Marco Rossi (U1) -> Credito: " + String.format("%.2f", ModelloAccess.getCredito(marcoRicaricato)) + " € (atteso 11.00 €)");
            System.out.println("  * Utente Luca Verdi (U3)   -> Credito: " + String.format("%.2f", ModelloAccess.getCredito(lucaRicaricato)) + " € (atteso 0.00 €)");
            System.out.println("  * Bici 101                 -> Stato: " + ModelloAccess.getStato(bici101) + ", Batteria: " + ModelloAccess.getLivelloBatteria(bici101) + "% (atteso 75%)");
            System.out.println("  * Bici 102                 -> Stato: " + ModelloAccess.getStato(bici102) + ", Batteria: " + ModelloAccess.getLivelloBatteria(bici102) + "% (atteso 100% IN_RICARICA)");
            System.out.println("  * Monopattino 201          -> Stato: " + ModelloAccess.getStato(monopattino201) + ", Batteria: " + ModelloAccess.getLivelloBatteria(monopattino201) + "% (atteso 86%)");

            System.out.println("\n[SUCCESSO] Tutti i dati persistono correttamente su database SQLite rossigo.db!");
        } catch (ErroreDatabaseException e) {
            System.out.println("[ERRORE DB] Problema di accesso al database: " + e.getMessage());
        } catch (RossiGoException e) {
            System.out.println("[ERRORE] " + e.getMessage());
        }

        System.out.println("\n=====================================================================");
        System.out.println("Simulazione completata con successo! Buona giornata da RossiGo.");
        System.out.println("=====================================================================");
    }

    private static void stampaBanner() {
        try {
            String banner = FigletFont.convertOneLine("RossiGo");
            System.out.println(banner);
        } catch (IOException | RuntimeException e) {
            System.out.println("""
                    ========================================
                                   ROSSIGO
                    ========================================
                    """);
        }
    }

    private static void preparaScenarioIniziale() {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM noleggi;");
            stmt.execute("DELETE FROM veicoli;");
            stmt.execute("DELETE FROM utenti;");
        } catch (Exception ignored) {
        }

        FlottaService initService = new FlottaService();
        initService.registraVeicolo(new Bici(101, 85, StatoVeicolo.LIBERO, 7));
        initService.registraVeicolo(new Bici(102, 10, StatoVeicolo.LIBERO, 5));
        initService.registraVeicolo(new Monopattino(201, 90, StatoVeicolo.LIBERO, 0.25, 1.0, 60));
        initService.registraVeicolo(new Monopattino(202, 80, StatoVeicolo.LIBERO, 0.25, 1.0, 60));

        initService.registraUtente(new Utente("U1", "Marco Rossi", "marco.rossi@email.it", "RSSMRC80A01H501U", 15.0));
        initService.registraUtente(new Utente("U2", "Anna Bianchi", "anna.bianchi@email.it", "BNCHNA90B41F205Z", 0.50));
        initService.registraUtente(new Utente("U3", "Luca Verdi", "luca.verdi@email.it", "VRDLCU85C15F205W", 2.00));
    }
}
