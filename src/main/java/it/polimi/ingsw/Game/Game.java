package it.polimi.ingsw.Game;

import it.polimi.ingsw.Exceptions.InvalidBuildActionException;
import it.polimi.ingsw.Exceptions.InvalidMoveActionException;
import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.GodFactory;
import it.polimi.ingsw.Utils.Pair;

import java.security.InvalidParameterException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
    private List<Player> players;
    private int currentPlayer;
    private Storage storage;
    private Board board;

    public Game(List<Player> players) {
        this.players = players.stream()
                .sorted(Comparator.comparing(Player::getGodLikeLevel))
                .collect(Collectors.toList());
        List<String> godNames = this.players.stream()
                .map(p -> p.getGodName())
                .collect(Collectors.toList());
        List<Actions> actions = GodFactory.makeActions(godNames);
        this.players.forEach(p -> {
            int idx = godNames.indexOf(p.getGodName());
            p.setActions(actions.remove(idx));
            godNames.remove(idx);
        });

        this.currentPlayer = 0;
        this.storage = new Storage();
        this.board = new Board();
    }

    /**
     * The Move interface to the external world
     * @param fromX the starting X coordinate on the board
     * @param fromY the starting Y coordinate on the board
     * @param toX the destination X coordinate on the board
     * @param toY the destination Y coordinate on the board
     * @throws InvalidParameterException  when the positions aren't valid
     * @throws InvalidMoveActionException when the move isn't valid
     */
    public void Move(int fromX, int fromY, int toX, int toY) throws InvalidParameterException, InvalidMoveActionException {
        Player p = players.get(currentPlayer);
        Pair<Worker, Tile> action = parseAction(fromX, fromY, toX, toY);
        Worker w = action.getFirst();
        Tile to = action.getSecond();

        if (p.getActions().canMove() && p.getActions().validMove(w, to)) {
            p.getActions().doMove(w, to);
        } else {
            String errorMessage = "This player cannot move";
            if (p.getActions().canMove()) {
                errorMessage += " to the desired position";
            }
            throw new InvalidMoveActionException(errorMessage);
        }
    }

    /**
     * The Build interface to the external world
     * @param fromX the starting X coordinate on the board
     * @param fromY the starting Y coordinate on the board
     * @param toX the destination X coordinate on the board
     * @param toY the destination Y coordinate on the board
     * @throws InvalidParameterException  when the positions aren't valid
     * @throws InvalidBuildActionException when the build isn't valid
     */
    public void Build(int fromX, int fromY, int toX, int toY, int lvl) throws InvalidParameterException, InvalidBuildActionException {
        Player p = players.get(currentPlayer);
        Pair<Worker, Tile> action = parseAction(fromX, fromY, toX, toY);
        Worker w = action.getFirst();
        Tile to = action.getSecond();

        if (p.getActions().canBuild() && p.getActions().validBuild(w, to, lvl) && storage.getAvailable(lvl) > 0) {
            storage.retrieve(lvl);
            p.getActions().doBuild(w, to, lvl);
        } else {
            String errorMessage = "This player cannot build";
            if (p.getActions().canMove()) {
                errorMessage += " a level" + lvl + " block to the desired position";
            }
            throw new InvalidBuildActionException(errorMessage);
        }
    }

    public void EndTurn() {
        currentPlayer++;
        if (currentPlayer == players.size())
            currentPlayer = 0;

        players.get(currentPlayer).getActions().beginTurn();
    }

    private Pair<Worker, Tile> parseAction(int fromX, int fromY, int toX, int toY) throws InvalidParameterException {
        Tile from, to;
        try {
            from = board.getAt(fromX, fromY);
            to = board.getAt(toX, toY);
        } catch(IndexOutOfBoundsException ex) {
            throw new InvalidParameterException(ex.getMessage());
        }
        Worker w = from.getOccupant();
        if (from.getOccupant() == null) {
            throw new InvalidParameterException("The specified starting tile is not hosting a worker");
        }

        return new Pair(w, to);
    }
}
