package org.example.eccezioni;

public class EmailNonValidaException extends RossiGoException{
    public EmailNonValidaException(String message) {
        super(message);
    }

    public EmailNonValidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
