package it.polimi.ingsw.Exceptions;

public class InvalidMoveActionException extends Exception {
    public InvalidMoveActionException(String errorMessage) {
        super(errorMessage);
    }
}
