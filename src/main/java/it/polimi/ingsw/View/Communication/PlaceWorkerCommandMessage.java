package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Exceptions.InvalidCommandException;

import java.util.Scanner;

public class PlaceWorkerCommandMessage extends CommandMessage {
    private final int x;
    private final int y;

    public PlaceWorkerCommandMessage(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static PlaceWorkerCommandMessage fromScanner(Scanner in) throws InvalidCommandException {
        try {
            return new PlaceWorkerCommandMessage(in.nextInt(), in.nextInt());
        } catch (Exception ex) {
            throw new InvalidCommandException("Invalid parameters for action `place`");
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "place " + x + "," + y;
    }
}
