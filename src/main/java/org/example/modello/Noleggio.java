package org.example.modello;
import java.time.LocalTime;

public class Noleggio{
    Utente utente;
    Veicolo veicolo;
    LocalTime orarioInizio;
    LocalTime orarioFine;

    public Noleggio(){
        utente=null;
        veicolo=null;
        orarioInizio= LocalTime.now();
        orarioFine= LocalTime.now();
    }

    public Noleggio(Utente utente, Veicolo veicolo, LocalTime orarioInizio, LocalTime orarioFine) {
        this.utente=utente;
        this.veicolo=veicolo;
        this.orarioInizio=orarioInizio;
        this.orarioFine=orarioFine;
    }
}
