package org.example.modello;

public class Bici extends Veicolo{
    double prezzoPerMinuto;
    double ConsumoPerMinuto;
    int numeroMarcie;
    public Bici() {
        super(0, 0, StatoVeicolo.LIBERO);
        ConsumoPerMinuto = 0.5;
        prezzoPerMinuto = 0.15;
        numeroMarcie = 5;
    }

    public Bici(int codice, int livellobatteria, StatoVeicolo statoveicolo, int numeroMarcie){
        super(codice, livellobatteria, statoveicolo);
        prezzoPerMinuto = 0.15;
        ConsumoPerMinuto = 0.5;
        this.numeroMarcie = numeroMarcie;
    }

    public int getNumeroMarcie() {
        return numeroMarcie;
    }

    @Override
    Double CalcolaCosto(int minuti) {
        return minuti*prezzoPerMinuto;
    }

    @Override
    Double ConsumoBatteriaPerMinuto() {
        return ConsumoPerMinuto;
    }
}