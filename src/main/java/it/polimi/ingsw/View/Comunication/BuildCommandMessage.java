package it.polimi.ingsw.View.Comunication;

import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Game.Player;

import java.util.Scanner;

public class BuildCommandMessage extends Message{
    private final Player player;
    private final int fromX;
    private final int fromY;
    private final int toX;
    private final int toY;
    private final int block;

    public BuildCommandMessage(Player player, int fromX, int fromY, int toX, int toY, int block) {
        this.player = player;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.block = block;
    }

    // TODO: Maybe this belongs to a separate parser class
    public static BuildCommandMessage fromScanner(Player player, Scanner in) throws InvalidCommandException {
        try {
            return new BuildCommandMessage(player, in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
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

    public Player getPlayer() {
        return player;
    }
}
