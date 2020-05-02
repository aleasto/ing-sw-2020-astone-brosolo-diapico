package it.polimi.ingsw.Game;

import it.polimi.ingsw.Exceptions.InvalidBuildActionException;
import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Exceptions.InvalidMoveActionException;
import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.GodFactory;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.View.Comunication.BuildCommandMessage;
import it.polimi.ingsw.View.Comunication.Dispatchers.NextActionsUpdateDispatcher;
import it.polimi.ingsw.View.Comunication.Dispatchers.TextDispatcher;
import it.polimi.ingsw.View.Comunication.Listeners.NextActionsUpdateListener;
import it.polimi.ingsw.View.Comunication.Listeners.TextListener;
import it.polimi.ingsw.View.Comunication.MoveCommandMessage;
import it.polimi.ingsw.View.Comunication.NextActionsUpdateMessage;
import it.polimi.ingsw.View.Comunication.TextMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Game implements NextActionsUpdateDispatcher, TextDispatcher {
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
            Pair<List<MoveCommandMessage>, List<BuildCommandMessage>> nextActions = computeAvailableActions();
            notifyNextActionsUpdate(new NextActionsUpdateMessage(nextActions.getFirst(), nextActions.getSecond()));
            notifyText(new TextMessage("Ok!"));
        } catch (InvalidCommandException | InvalidMoveActionException ex) {
            notifyText(new TextMessage(ex.getMessage()));
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
            notifyWaitingForNextAction(computeAvailableActions());
        } catch (InvalidCommandException | InvalidBuildActionException ex) {
            notifyText(new TextMessage(ex.getMessage()));
        }
    }

    public void EndTurn() {
        currentPlayer++;
        if (currentPlayer == players.size())
            currentPlayer = 0;

        players.get(currentPlayer).getActions().beginTurn();
        notifyWaitingForNextAction(computeAvailableActions());
    }

    // Helper method to notifyTextActionsUpdate + notifyText
    private void notifyWaitingForNextAction(Pair<List<MoveCommandMessage>, List<BuildCommandMessage>> nextActions){
        notifyNextActionsUpdate(new NextActionsUpdateMessage(nextActions.getFirst(), nextActions.getSecond()));
        notifyText(new TextMessage("Ok!"));
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

    private Pair<List<MoveCommandMessage>, List<BuildCommandMessage>> computeAvailableActions() {
        Player p = players.get(currentPlayer);
        List<MoveCommandMessage> availMoves = new ArrayList<>();
        List<BuildCommandMessage> availBuilds = new ArrayList<>();
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
                            for (int z = 0; z < Tile.getMaxHeight(); z++) {
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

    final List<TextListener> textListeners = new ArrayList<>();
    @Override
    public void addTextListener(TextListener listener){
        synchronized (textListeners) {
            textListeners.add(listener);
        }
        onRegisterForText(listener);
    }
    @Override
    public void removeTextListener(TextListener listener){
        synchronized (textListeners) {
            textListeners.remove(listener);
        }
    }
    @Override
    public void notifyText(TextMessage message) {
        synchronized (textListeners) {
            for (TextListener listener : textListeners) {
                listener.onText(message);
            }
        }
    }
    @Override
    public void onRegisterForText(TextListener listener) {
        listener.onText(new TextMessage("Welcome!"));
    }

    final List<NextActionsUpdateListener> nextActionsUpdateListeners = new ArrayList<>();
    @Override
    public void addNextActionsUpdateListener(NextActionsUpdateListener listener){
        synchronized (nextActionsUpdateListeners) {
            nextActionsUpdateListeners.add(listener);
        }
        onRegisterForNextActionsUpdate(listener);
    }
    @Override
    public void removeNextActionsUpdateListener(NextActionsUpdateListener listener){
        synchronized (nextActionsUpdateListeners) {
            nextActionsUpdateListeners.remove(listener);
        }
    }
    @Override
    public void notifyNextActionsUpdate(NextActionsUpdateMessage message) {
        synchronized (nextActionsUpdateListeners) {
            for (NextActionsUpdateListener listener : nextActionsUpdateListeners) {
                listener.onNextActionsUpdate(message);
            }
        }
    }
    @Override
    public void onRegisterForNextActionsUpdate(NextActionsUpdateListener listener) {
        Pair<List<MoveCommandMessage>, List<BuildCommandMessage>> nextActions = computeAvailableActions();
        listener.onNextActionsUpdate(new NextActionsUpdateMessage(nextActions.getFirst(), nextActions.getSecond()));
    }
}
