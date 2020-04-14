package it.polimi.ingsw.Exceptions;

public class InvalidBuildActionException extends Exception {
    public InvalidBuildActionException(String errorMessage) {
        super(errorMessage);
    }
}
