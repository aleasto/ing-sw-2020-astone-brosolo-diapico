package it.polimi.ingsw.Game;

import it.polimi.ingsw.Exceptions.InvalidBuildActionException;
import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Exceptions.InvalidMoveActionException;
import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.GodFactory;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.View.Communication.BuildCommandMessage;
import it.polimi.ingsw.View.Communication.MoveCommandMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Game {
    private List<Player> players;
    private int currentPlayer;
    private Worker currentWorker; // currentPlayer may only use one Worker during his turn
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
        for (int i = 0; i < players.size(); i++) {
            this.players.get(i).setActions(actions.get(i));
        }

        this.currentPlayer = 0;
        this.storage = new Storage();
        this.board = new Board();
    }

    public Board getBoard() {
        return this.board;
    }

    public Storage getStorage() {
        return this.storage;
    }

    /**
     * The Move interface to the external world
     * @param player the player that invokes this call
     * @param fromX the starting X coordinate on the board
     * @param fromY the starting Y coordinate on the board
     * @param toX the destination X coordinate on the board
     * @param toY the destination Y coordinate on the board
     */
    public void Move(Player player, int fromX, int fromY, int toX, int toY) throws InvalidCommandException, InvalidMoveActionException {
        Player p = players.get(currentPlayer);
        if (!p.equals(player)) {
            throw new InvalidCommandException("Wait for your turn ffs");
        }

        Pair<Worker, Tile> action = parseAction(fromX, fromY, toX, toY);
        Worker w = action.getFirst();
        Tile to = action.getSecond();
        if (p.getActions().canMove() && p.getActions().validMove(w, to)) {
            currentWorker = w;
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
     * @param player the player that invokes this call
     * @param fromX the starting X coordinate on the board
     * @param fromY the starting Y coordinate on the board
     * @param toX the destination X coordinate on the board
     * @param toY the destination Y coordinate on the board
     */
    public void Build(Player player, int fromX, int fromY, int toX, int toY, int lvl) throws InvalidCommandException, InvalidBuildActionException {
        Player p = players.get(currentPlayer);
        if (!p.equals(player)) {
            throw new InvalidCommandException("Wait for your turn ffs");
        }

        Pair<Worker, Tile> action = parseAction(fromX, fromY, toX, toY);
        Worker w = action.getFirst();
        Tile to = action.getSecond();
        if (p.getActions().canBuild() && p.getActions().validBuild(w, to, lvl) && storage.getAvailable(lvl) > 0) {
            currentWorker = w;
            storage.retrieve(lvl);
            p.getActions().doBuild(w, to, lvl);
        } else {
            String errorMessage = "This player cannot build";
            if (p.getActions().canBuild()) {
                errorMessage += " a level" + lvl + " block to the desired position";
            }
            throw new InvalidBuildActionException(errorMessage);
        }
    }

    /**
     * End the current turn
     * @param player the player that invokes this call
     * @return the next player to play
     */
    public Player EndTurn(Player player) throws InvalidCommandException {
        Player p = players.get(currentPlayer);
        if (!p.equals(player)) {
            throw new InvalidCommandException("Wait for your turn ffs");
        }

        currentPlayer++;
        if (currentPlayer == players.size())
            currentPlayer = 0;
        currentWorker = null;

        players.get(currentPlayer).getActions().beginTurn();
        return players.get(currentPlayer);
    }

    private Pair<Worker, Tile> parseAction(int fromX, int fromY, int toX, int toY) throws InvalidCommandException {
        Tile from, to;
        try {
            from = board.getAt(fromX, fromY);
            to = board.getAt(toX, toY);
        } catch(IndexOutOfBoundsException ex) {
            throw new InvalidCommandException("The specified tile(s) do not exist");
        }
        Worker w = from.getOccupant();
        if (w == null) {
            throw new InvalidCommandException("The specified starting tile is not hosting a worker");
        }
        if (currentWorker != null && !currentWorker.equals(w) || !players.get(currentPlayer).equals(w.getOwner())) {
            throw new InvalidCommandException("You cannot perform an action with the specified worker");
        }

        return new Pair(w, to);
    }

    public Pair<List<MoveCommandMessage>, List<BuildCommandMessage>> computeAvailableActions(Player player) {
        List<MoveCommandMessage> availMoves = new ArrayList<>();
        List<BuildCommandMessage> availBuilds = new ArrayList<>();
        Player p = players.get(currentPlayer);
        if (!p.equals(player)) {
            return new Pair(availMoves, availBuilds);
        }

        for (int fromX = 0; fromX < board.getDimX(); fromX++) {
            for (int fromY = 0; fromY < board.getDimY(); fromY++) {
                for (int toX = 0; toX < board.getDimX(); toX++) {
                    for (int toY = 0; toY < board.getDimY(); toY++) {
                        try {
                            Pair<Worker, Tile> action = parseAction(fromX, fromY, toX, toY);
                            if (p.getActions().canMove() &&
                                    p.getActions().validMove(action.getFirst(), action.getSecond())) {
                                availMoves.add(new MoveCommandMessage(p, fromX, fromY, toX, toY));
                            }
                            for (int z = 0; z <= Tile.getMaxHeight(); z++) {
                                if (p.getActions().canBuild() &&
                                        p.getActions().validBuild(action.getFirst(), action.getSecond(), z)) {
                                    availBuilds.add(new BuildCommandMessage(p, fromX, fromY, toX, toY, z));
                                }
                            }
                        } catch (Exception ex) { } // It's perfectly fine to fail here
                    }
                }
            }
        }
        return new Pair(availMoves, availBuilds);
    }
}
