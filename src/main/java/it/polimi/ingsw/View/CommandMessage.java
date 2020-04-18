package it.polimi.ingsw.View;

import it.polimi.ingsw.Game.Player;

import java.util.Scanner;

public class CommandMessage {
    // TODO: Make these an enum
    public static final char MOVE = 'm';
    public static final char BUILD = 'b';

    private Player player; // TODO: This better be a UID for the player, rather than the full object.
    private char action;
    private int fromX;
    private int fromY;
    private int toX;
    private int toY;
    private int toZ;

    public CommandMessage(Player player, char action, int fromX, int fromY, int toX, int toY, int toZ) {
        this.player = player;
        this.action = action;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.toZ = toZ;
    }

    public static CommandMessage parseCommand(Player player, String in) throws IllegalArgumentException {
        Scanner scanner = new Scanner(in);
        scanner.useDelimiter(" ");
        char action = scanner.next().charAt(0);
        if (action != MOVE && action != BUILD)
            throw new IllegalArgumentException(action + " is not a valid action");

        int fromX = scanner.nextInt();
        int fromY = scanner.nextInt();
        int toX = scanner.nextInt();
        int toY = scanner.nextInt();
        int toZ = scanner.hasNextInt() ? scanner.nextInt() : -1;
        return new CommandMessage(player, action, fromX, fromY, toX, toY, toZ);
    }

    public Player getPlayer() {
        return player;
    }

    public char getAction() {
        return action;
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

    public int getToZ() {
        return toZ;
    }

}
