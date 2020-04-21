package it.polimi.ingsw.Game;

import it.polimi.ingsw.Exceptions.InvalidBuildActionException;
import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Exceptions.InvalidMoveActionException;
import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.GodFactory;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.View.CommandMessage;
import it.polimi.ingsw.View.CommandResponse;
import it.polimi.ingsw.View.Observable;
import it.polimi.ingsw.View.Observer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Game extends Observable<CommandResponse> implements Observer<CommandMessage> {
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
     * @param fromX the starting X coordinate on the board
     * @param fromY the starting Y coordinate on the board
     * @param toX the destination X coordinate on the board
     * @param toY the destination Y coordinate on the board
     */
    public void Move(int fromX, int fromY, int toX, int toY) {
        Player p = players.get(currentPlayer);
        try {
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
            notifyChange(new CommandResponse("Ok!", computeAvailableActions()));
        } catch (InvalidCommandException | InvalidMoveActionException ex) {
            notifyChange(new CommandResponse(ex.getMessage()));
        }
    }

    /**
     * The Build interface to the external world
     * @param fromX the starting X coordinate on the board
     * @param fromY the starting Y coordinate on the board
     * @param toX the destination X coordinate on the board
     * @param toY the destination Y coordinate on the board
     */
    public void Build(int fromX, int fromY, int toX, int toY, int lvl) {
        Player p = players.get(currentPlayer);
        try {
            Pair<Worker, Tile> action = parseAction(fromX, fromY, toX, toY);
            Worker w = action.getFirst();
            Tile to = action.getSecond();
            if (p.getActions().canBuild() && p.getActions().validBuild(w, to, lvl) && storage.getAvailable(lvl) > 0) {
                storage.retrieve(lvl);
                p.getActions().doBuild(w, to, lvl);
            } else {
                String errorMessage = "This player cannot build";
                if (p.getActions().canBuild()) {
                    errorMessage += " a level" + lvl + " block to the desired position";
                }
                throw new InvalidBuildActionException(errorMessage);
            }
            notifyChange(new CommandResponse("Ok!", computeAvailableActions()));
        } catch (InvalidCommandException | InvalidBuildActionException ex) {
            notifyChange(new CommandResponse(ex.getMessage()));
        }
    }

    public void EndTurn() {
        currentPlayer++;
        if (currentPlayer == players.size())
            currentPlayer = 0;

        players.get(currentPlayer).getActions().beginTurn();
        notifyChange(new CommandResponse("Ok!", computeAvailableActions()));
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
        if (from.getOccupant() == null) {
            throw new InvalidCommandException("The specified starting tile is not hosting a worker");
        }

        return new Pair(w, to);
    }

    private List<CommandMessage> computeAvailableActions() {
        Player p = players.get(currentPlayer);
        List<CommandMessage> avail = new ArrayList<>();
        for (int fromX = 0; fromX < board.getDimX(); fromX++) {
            for (int fromY = 0; fromY < board.getDimY(); fromY++) {
                for (int toX = 0; toX < board.getDimX(); toX++) {
                    for (int toY = 0; toY < board.getDimY(); toY++) {
                        try {
                            Pair<Worker, Tile> action = parseAction(fromX, fromY, toX, toY);
                            if (p.getActions().canMove() &&
                                    p.getActions().validMove(action.getFirst(), action.getSecond())) {
                                avail.add(new CommandMessage(p, CommandMessage.Action.MOVE,
                                        fromX, fromY, toX, toY, 0 /* unused */));
                            }
                            for (int z = 0; z < Tile.getMaxHeight(); z++) {
                                if (p.getActions().canBuild() &&
                                        p.getActions().validBuild(action.getFirst(), action.getSecond(), z)) {
                                    avail.add(new CommandMessage(p, CommandMessage.Action.BUILD,
                                            fromX, fromY, toX, toY, z));
                                }
                            }
                        } catch (Exception ex) { } // It's perfectly fine to fail here
                    }
                }
            }
        }
        return avail;
    }

    @Override
    public void onChange(CommandMessage message) {
        // We received a message!
        if (message.getPlayer() != players.get(currentPlayer)) {
            notifyChange(new CommandResponse("Whatcha doing sending me commands when its not your turn?"));
        }

        switch (message.getAction()) {
            case MOVE:
                Move(message.getFromX(), message.getFromY(), message.getToX(), message.getToY());
                break;
            case BUILD:
                Build(message.getFromX(), message.getFromY(), message.getToX(), message.getToY(), message.getToZ());
                break;
            case ENDTURN:
                EndTurn();
        }
    }

    @Override
    public void onRegister(Observer<CommandResponse> obs) {
        // Send initial data to the newly connected observer
        obs.onChange(new CommandResponse("Welcome!", computeAvailableActions()));
    }
}
