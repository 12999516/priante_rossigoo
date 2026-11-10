package org.example.eccezioni;

public class UtenteNonTrovatoException extends RuntimeException {
    public UtenteNonTrovatoException(String message) {
        super(message);
    }

    public UtenteNonTrovatoException(String message, Throwable cause) {
        super(message, cause);
    }
}
