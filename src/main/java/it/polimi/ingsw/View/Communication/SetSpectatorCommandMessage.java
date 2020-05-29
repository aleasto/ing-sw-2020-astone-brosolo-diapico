package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Exceptions.InvalidCommandException;

import java.util.Scanner;

public class SetSpectatorCommandMessage extends CommandMessage {
    private final boolean spectator;

    public SetSpectatorCommandMessage(boolean spectator) {
        this.spectator = spectator;
    }

    public static SetSpectatorCommandMessage fromScanner(Scanner in) throws InvalidCommandException {
        try {
            String spectator = in.next();
            if (spectator.equals("on")) {
                return new SetSpectatorCommandMessage(true);
            } else if (spectator.equals("off")) {
                return new SetSpectatorCommandMessage(false);
            } else {
                throw new InvalidCommandException("Invalid parameter for action `spectator`");
            }
        } catch(Exception ex) {
            throw new InvalidCommandException("Invalid parameter for action `spectator`");
        }
    }

    public boolean spectatorOn() {
        return spectator;
    }

    @Override
    public String toString() {
        return "spectator " + (spectatorOn() ? "on" : "off");
    }
}
