package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Game.Player;

import java.util.Scanner;

public class MoveCommandMessage extends Message {
    private final Player player;
    private final int fromX;
    private final int fromY;
    private final int toX;
    private final int toY;

    public MoveCommandMessage(Player player, int fromX, int fromY, int toX, int toY) {
        this.player = player;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    // TODO: Maybe this belongs to a separate parser class
    public static MoveCommandMessage fromScanner(Player player, Scanner in) throws InvalidCommandException {
        try {
            return new MoveCommandMessage(player, in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
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

    public Player getPlayer() {
        return player;
    }
}
