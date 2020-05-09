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
    private final List<Player> players;
    private final int challengerPlayer;
    private int currentPlayer;
    private Worker currentWorker; // currentPlayer may only use one Worker during his turn
    private final Storage storage;
    private final Board board;
    private List<String> godPool;

    // Game state
    private GameState state;

    public Game(List<Player> players) {
        this.players = new ArrayList<Player>();
        this.players.addAll(players);
        this.storage = new Storage();
        this.board = new Board();
        this.challengerPlayer = IntStream.range(0, players.size()).boxed()
                .max(Comparator.comparing(i -> players.get(i).getGodLikeLevel())).orElse(-1);
        this.currentPlayer = challengerPlayer;

        this.state = new GodPoolSelectionState();
    }

    public Board getBoard() {
        return this.board;
    }

    public Storage getStorage() {
        return this.storage;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    public List<String> getGodPool() {
        return this.godPool;
    }

    private void checkTurn(Player player) throws InvalidCommandException {
        Player p = players.get(currentPlayer);
        if (!p.equals(player)) {
            throw new InvalidCommandException("It's not your turn");
        }
    }

    public void SetGodPool(Player player, List<String> godPool) throws InvalidCommandException {
        checkTurn(player);
        state.SetGodPool(player, godPool);
    }

    public void SetGod(Player player, String god) throws InvalidCommandException {
        checkTurn(player);
        state.SetGod(player, god);
    }

    public void PlaceWorker(Player player, int x, int y) throws InvalidCommandException {
        state.PlaceWorker(player, x, y);
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
        checkTurn(player);

        state.Move(player, fromX, fromY, toX, toY);
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
        checkTurn(player);
        state.Build(player, fromX, fromY, toX, toY, lvl);
    }

    /**
     * End the current turn
     * @param player the player that invokes this call
     * @return the next player to play
     */
    public Player EndTurn(Player player) throws InvalidCommandException {
        checkTurn(player);

        int newPlayer = currentPlayer + 1;
        if (newPlayer == players.size())
            newPlayer = 0;

        state.EndTurn(player /* previous */, players.get(newPlayer) /* new */);
        currentPlayer = newPlayer;  // only change the current player if stats.EndTurn did not throw
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
                                availMoves.add(new MoveCommandMessage(fromX, fromY, toX, toY));
                            }
                            for (int z = 0; z <= Tile.getMaxHeight(); z++) {
                                if (p.getActions().canBuild() &&
                                        p.getActions().validBuild(action.getFirst(), action.getSecond(), z)) {
                                    availBuilds.add(new BuildCommandMessage(fromX, fromY, toX, toY, z));
                                }
                            }
                        } catch (Exception ex) { } // It's perfectly fine to fail here
                    }
                }
            }
        }
        return new Pair(availMoves, availBuilds);
    }

    private List<Worker> getWorkersOf(Player player) {
        List<Worker> workers = new ArrayList<>();
        for (int i = 0; i < board.getDimX(); i++) {
            for (int j = 0; j < board.getDimY(); j++) {
                Tile tile = board.getAt(i, j);
                Worker w = tile.getOccupant();
                if (w != null && player.equals(w.getOwner())) {
                    workers.add(w);
                }
            }
        }
        return workers;
    }

    //<editor-fold desc="Game state pattern">
    public class GodPoolSelectionState implements GameState {
        @Override
        public void SetGodPool(Player player, List<String> godPool) throws InvalidCommandException {
            Player challenger = players.get(challengerPlayer);
            if (!challenger.equals(player)) {
                throw new InvalidCommandException("You are not allowed to change the god pool");
            }
            if (godPool.size() != players.size() || !GodFactory.getGodNames().containsAll(godPool)) {
                throw new InvalidCommandException("Invalid god pool");
            }
            Game.this.godPool = godPool;
        }

        @Override
        public void EndTurn(Player previousPlayer, Player newPlayer) throws InvalidCommandException {
            if (godPool == null) {
                throw new InvalidCommandException("You must choose a god pool");
            }
            state = new GodSelectionState();
        }
    }

    public class GodSelectionState implements GameState {
        @Override
        public void SetGod(Player player, String god) throws InvalidCommandException {
            Player p = players.get(currentPlayer);
            if (godPool == null || !p.equals(player)) {
                throw new InvalidCommandException("You are not allowed to change your god");
            }
            if (!godPool.contains(god)) {
                throw new InvalidCommandException("Invalid god name");
            }
            godPool.remove(god);
            p.setGodName(god);
        }

        @Override
        public void EndTurn(Player previousPlayer, Player newPlayer) throws InvalidCommandException {
             if (previousPlayer.getGodName() == null || previousPlayer.getGodName().isEmpty()) {
                 throw new InvalidCommandException("You must choose a god");
             }

            if (godPool.size() == 0) {
                // Everybody chose their god. Make Actions and move to next stage
                List<String> godNames = players.stream()
                        .map(Player::getGodName)
                        .collect(Collectors.toList());
                List<Actions> actions = GodFactory.makeActions(godNames);
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).setActions(actions.get(i));
                }

                state = new WorkerPlacingState();
            }
        }
    }

    public class WorkerPlacingState implements GameState {
        private static final int maxWorkers = 2;

        @Override
        public void PlaceWorker(Player player, int x, int y) throws InvalidCommandException {
            if (getWorkersOf(player).size() >= maxWorkers) {
                throw new InvalidCommandException("You can only place down 2 workers");
            }
            try {
                Tile tile = board.getAt(x, y);
                if (!tile.isEmpty()) {
                    throw new InvalidCommandException("The specified tile is already occupied");
                }
                new Worker(player, tile);
            } catch (IndexOutOfBoundsException ex) {
                throw new InvalidCommandException("The specified tile does not exist");
            }
        }

        @Override
        public void EndTurn(Player previousPlayer, Player newPlayer) throws InvalidCommandException {
            if (getWorkersOf(previousPlayer).size() != maxWorkers) {
                throw new InvalidCommandException("You must place down 2 workers");
            }

            int sumWorkers = players.stream().mapToInt(p -> getWorkersOf(p).size()).sum();
            if (sumWorkers == players.size() * maxWorkers) {
                // Everybody placed down their workers. Let the game begin
                state = new PlayingState();
            }
        }
    }

    public class PlayingState implements GameState {
        @Override
        public void Move(Player player, int fromX, int fromY, int toX, int toY) throws InvalidCommandException, InvalidMoveActionException {
            Pair<Worker, Tile> action = parseAction(fromX, fromY, toX, toY);
            Worker w = action.getFirst();
            Tile to = action.getSecond();
            if (player.getActions().canMove() && player.getActions().validMove(w, to)) {
                currentWorker = w;
                player.getActions().doMove(w, to);
            } else {
                String errorMessage = "This player cannot move";
                if (player.getActions().canMove()) {
                    errorMessage += " to the desired position";
                }
                throw new InvalidMoveActionException(errorMessage);
            }
        }

        @Override
        public void Build(Player player, int fromX, int fromY, int toX, int toY, int lvl) throws InvalidCommandException, InvalidBuildActionException {
            Pair<Worker, Tile> action = parseAction(fromX, fromY, toX, toY);
            Worker w = action.getFirst();
            Tile to = action.getSecond();
            if (player.getActions().canBuild() && player.getActions().validBuild(w, to, lvl) && storage.getAvailable(lvl) > 0) {
                currentWorker = w;
                storage.retrieve(lvl);
                player.getActions().doBuild(w, to, lvl);
            } else {
                String errorMessage = "This player cannot build";
                if (player.getActions().canBuild()) {
                    errorMessage += " a level" + lvl + " block to the desired position";
                }
                throw new InvalidBuildActionException(errorMessage);
            }
        }

        @Override
        public void EndTurn(Player previousPlayer, Player newPlayer) throws InvalidCommandException {
            currentWorker = null;
            newPlayer.getActions().beginTurn();
        }
    }
    //</editor-fold>
}
