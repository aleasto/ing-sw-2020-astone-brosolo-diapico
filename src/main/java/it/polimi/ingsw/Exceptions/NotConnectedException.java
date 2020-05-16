package it.polimi.ingsw.Exceptions;

public class NotConnectedException extends RuntimeException {
    public NotConnectedException(String errorMessage) {
        super(errorMessage);
    }
}
