package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.InvalidCommandException;

import java.util.Scanner;

public interface ParserState {
    void handleInput(Scanner s) throws InvalidCommandException;
}
