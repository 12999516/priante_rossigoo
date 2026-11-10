package org.example.modello;

import java.time.LocalTime;

public class ModelloAccess {

    public static int getCodice(Veicolo v) {
        return v.codice;
    }

    public static int getLivelloBatteria(Veicolo v) {
        return v.livelloBatteria;
    }

    public static void setLivelloBatteria(Veicolo v, int livello) {
        v.livelloBatteria = livello;
    }

    public static StatoVeicolo getStato(Veicolo v) {
        return v.stato;
    }

    public static void setStato(Veicolo v, StatoVeicolo stato) {
        v.stato = stato;
    }

    public static double calcolaCosto(Veicolo v, int minuti) {
        return v.CalcolaCosto(minuti);
    }

    public static double consumoBatteriaPerMinuto(Veicolo v) {
        return v.ConsumoBatteriaPerMinuto();
    }

    public static int getNumeroMarcie(Bici b) {
        return b.getNumeroMarcie();
    }

    public static int getVelocitaMassima(Monopattino m) {
        return m.VelocitaMassima;
    }

    public static String getId(Utente u) {
        return u.id;
    }

    public static String getNome(Utente u) {
        return u.nome;
    }

    public static String getEmail(Utente u) {
        return u.email;
    }

    public static String getCodiceFiscale(Utente u) {
        return u.codiceFiscale;
    }

    public static double getCredito(Utente u) {
        return u.Credito;
    }

    public static void setCredito(Utente u, double credito) {
        u.Credito = credito;
    }

    public static Utente getUtente(Noleggio n) {
        return n.utente;
    }

    public static Veicolo getVeicolo(Noleggio n) {
        return n.veicolo;
    }

    public static LocalTime getOrarioInizio(Noleggio n) {
        return n.orarioInizio;
    }

    public static LocalTime getOrarioFine(Noleggio n) {
        return n.orarioFine;
    }

    public static void setOrarioFine(Noleggio n, LocalTime orarioFine) {
        n.orarioFine = orarioFine;
    }
}
