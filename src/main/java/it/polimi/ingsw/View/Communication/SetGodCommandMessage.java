package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Exceptions.InvalidCommandException;

import java.util.Scanner;

public class SetGodCommandMessage extends CommandMessage {
    private String godName;

    public SetGodCommandMessage(String godName) {
        this.godName = godName;
    }

    public static SetGodCommandMessage fromScanner(Scanner in) throws InvalidCommandException {
        try {
            return new SetGodCommandMessage(in.next());
        } catch (Exception ex) {
            throw new InvalidCommandException("Invalid parameters for action `god`");
        }
    }

    public String getGodName() {
        return godName;
    }

    @Override
    public String toString() {
        return "god " + godName;
    }
}
