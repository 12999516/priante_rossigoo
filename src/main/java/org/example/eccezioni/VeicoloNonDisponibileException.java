package org.example.eccezioni;

public class VeicoloNonDisponibileException extends RossiGoException {
    public VeicoloNonDisponibileException(String message) {
        super(message);
    }
    public VeicoloNonDisponibileException (String message, Throwable cause) {super(message, cause);}
}
