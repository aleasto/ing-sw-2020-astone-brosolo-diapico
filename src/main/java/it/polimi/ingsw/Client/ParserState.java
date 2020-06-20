package it.polimi.ingsw.Client;

import it.polimi.ingsw.Exceptions.InvalidCommandException;

import java.util.Scanner;

public interface ParserState {
    void handleInput(Scanner s) throws InvalidCommandException;
}
