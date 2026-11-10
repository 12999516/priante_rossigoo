package org.example.eccezioni;

public class CodiceFiscaleNonValidoException extends RossiGoException {
    public CodiceFiscaleNonValidoException(String message) {
        super(message);
    }

    public CodiceFiscaleNonValidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
