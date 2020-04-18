package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Game.Player;

import java.util.IllegalFormatException;
import java.util.Scanner;

public class CommandMessage {
    public enum Action {
        MOVE {
            @Override
            CommandMessage parseCommand(Player player, Scanner in) throws InvalidCommandException{
                try {
                    int fromX = in.nextInt();
                    int fromY = in.nextInt();
                    int toX = in.nextInt();
                    int toY = in.nextInt();
                    return new CommandMessage(player, this, fromX, fromY, toX, toY, 0 /* unused */);
                } catch (Exception ex) {
                    throw new InvalidCommandException("Invalid parameters for action `" + this + "`");
                }
            }
        },
        BUILD {
            @Override
            CommandMessage parseCommand(Player player, Scanner in) throws InvalidCommandException {
                try {
                    int fromX = in.nextInt();
                    int fromY = in.nextInt();
                    int toX = in.nextInt();
                    int toY = in.nextInt();
                    int toZ = in.nextInt();
                    return new CommandMessage(player, this, fromX, fromY, toX, toY, toZ);
                } catch (Exception ex) {
                    throw new InvalidCommandException("Invalid parameters for action `" + this + "`");
                }
            }
        };

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }

        abstract CommandMessage parseCommand(Player player, Scanner in) throws InvalidCommandException;
    }

    private Player player; // TODO: This better be a UID for the player, rather than the full object.
    private Action action;
    private int fromX;
    private int fromY;
    private int toX;
    private int toY;
    private int toZ;

    public CommandMessage(Player player, Action action, int fromX, int fromY, int toX, int toY, int toZ) {
        this.player = player;
        this.action = action;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.toZ = toZ;
    }

    public static CommandMessage parseCommand(Player player, String in) throws InvalidCommandException {
        Scanner scanner = new Scanner(in);
        scanner.useDelimiter(",| |\\n"); // separators are comma, space and newline
        String actionName = scanner.next();
        Action action;
        try {
            action = Action.valueOf(actionName.toUpperCase());
        } catch (IllegalArgumentException ex){
            throw new InvalidCommandException("`" + actionName + "` is not a valid action");
        }
        return action.parseCommand(player, scanner);
    }

    public Player getPlayer() {
        return player;
    }

    public Action getAction() {
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
