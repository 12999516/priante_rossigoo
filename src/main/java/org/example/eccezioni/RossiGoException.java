package org.example.eccezioni;

public class RossiGoException extends RuntimeException {
    public RossiGoException(String message) {
        super(message);
    }

    public RossiGoException(String message, Throwable cause) {
        super(message, cause);
    }
}
