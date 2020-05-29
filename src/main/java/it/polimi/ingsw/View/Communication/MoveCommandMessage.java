package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Game.Player;

import java.util.Scanner;

public class MoveCommandMessage extends CommandMessage {
    private final int fromX;
    private final int fromY;
    private final int toX;
    private final int toY;

    public MoveCommandMessage(int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    public static MoveCommandMessage fromScanner(Scanner in) throws InvalidCommandException {
        try {
            return new MoveCommandMessage(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
        } catch (Exception ex) {
            throw new InvalidCommandException("Invalid parameters for action `move`");
        }
    }

    public int getFromX() {
        return fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public int getToX() {
        return toX;
    }

    public int getToY() {
        return toY;
    }

    @Override
    public String toString() {
        return "move from " + fromX + "," + fromY + " to " + toX + "," + toY;
    }
}
