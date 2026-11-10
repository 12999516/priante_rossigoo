package org.example.modello;

public abstract class Veicolo {
    protected int codice;
    protected int livelloBatteria;
    protected StatoVeicolo stato;

    protected Veicolo(int codice, int livelloBatteria, StatoVeicolo stato){
        this.codice = codice;
        this.livelloBatteria = livelloBatteria;
        this.stato = stato;
    }

    abstract Double CalcolaCosto(int minuti);
    abstract Double ConsumoBatteriaPerMinuto();

}
