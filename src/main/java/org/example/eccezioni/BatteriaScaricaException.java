package org.example.eccezioni;

public class BatteriaScaricaException extends RossiGoException{
    public BatteriaScaricaException(String message) {
        super(message);
    }

    public BatteriaScaricaException(String message, Throwable cause) {
        super(message, cause);
    }
}
