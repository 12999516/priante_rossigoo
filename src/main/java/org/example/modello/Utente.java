package org.example.modello;
import org.apache.commons.validator.routines.EmailValidator;
import org.example.eccezioni.EmailNonValidaException;

public class Utente {
    String id;
    String nome;
    String email;
    String codiceFiscale;
    double Credito;

    public Utente() {
        id="0";
        nome= "";
        email="";
        codiceFiscale="";
        Credito=0.0;
    }

    public Utente(String id, String nome, String email, String codiceFiscale, double credito) {
        this.id = id;
        this.nome=nome;
        this.codiceFiscale=codiceFiscale;
        this.Credito=credito;
        if (validaMail(email)){
            this.email=email;
        }else {
            throw new EmailNonValidaException("la mail non è valida");
        }
    }

    private boolean validaMail(String email){

        EmailValidator validator = EmailValidator.getInstance();
        boolean isValid = validator.isValid(email);
        return isValid;
    }
}
