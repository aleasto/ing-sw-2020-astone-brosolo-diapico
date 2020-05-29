package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Game.Player;

import java.util.Scanner;

public class BuildCommandMessage extends CommandMessage {
    private final int fromX;
    private final int fromY;
    private final int toX;
    private final int toY;
    private final int block;

    public BuildCommandMessage(int fromX, int fromY, int toX, int toY, int block) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.block = block;
    }

    public static BuildCommandMessage fromScanner(Scanner in) throws InvalidCommandException {
        try {
            return new BuildCommandMessage(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
        } catch (Exception ex) {
            throw new InvalidCommandException("Invalid parameters for action `build`");
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

    public int getBlock() {
        return block;
    }

    @Override
    public String toString() {
        return "build " + fromX + "," + fromY + " to " + toX + "," + toY + " at level " + block;
    }
}
