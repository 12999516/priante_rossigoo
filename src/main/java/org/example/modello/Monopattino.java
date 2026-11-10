package org.example.modello;

public class Monopattino extends Veicolo{

    double TariffaPerMinuto;
    int VelocitaMassima;
    double ConsumoPerMinuto;

    protected Monopattino(int codice, int livelloBatteria, StatoVeicolo stato) {
        super(codice, livelloBatteria, stato);
        TariffaPerMinuto = 0.25;
        ConsumoPerMinuto = 1;
        VelocitaMassima = 60;
    }

    public Monopattino(int codice, int livelloBatteria, StatoVeicolo stato, double TariffaPerMinuto, double ConsumoPerMinuto, int VelocitaMassima){
        super(codice, livelloBatteria, stato);
        this.TariffaPerMinuto = TariffaPerMinuto;
        this.ConsumoPerMinuto = ConsumoPerMinuto;
        this.VelocitaMassima = VelocitaMassima;
    }

    @Override
    Double CalcolaCosto(int minuti) {
        return 0.0;
    }

    @Override
    Double ConsumoBatteriaPerMinuto() {
        return 0.0;
    }
}
