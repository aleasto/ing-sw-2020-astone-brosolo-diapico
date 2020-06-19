package it.polimi.ingsw.Game;

import it.polimi.ingsw.Exceptions.InvalidBuildActionException;
import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Exceptions.InvalidMoveActionException;
import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.GodFactory;
import it.polimi.ingsw.Server.Lobby;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.Communication.Broadcasters.EndGameEventBroadcaster;
import it.polimi.ingsw.View.Communication.Broadcasters.PlayerChoseGodEventBroadcaster;
import it.polimi.ingsw.View.Communication.Broadcasters.PlayerLoseEventBroadcaster;
import it.polimi.ingsw.View.Communication.Broadcasters.PlayerTurnUpdateBroadcaster;
import it.polimi.ingsw.View.Communication.Listeners.EndGameEventListener;
import it.polimi.ingsw.View.Communication.Listeners.PlayerChoseGodEventListener;
import it.polimi.ingsw.View.Communication.Listeners.PlayerLoseEventListener;
import it.polimi.ingsw.View.Communication.Listeners.PlayerTurnUpdateListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Game implements PlayerTurnUpdateBroadcaster, PlayerLoseEventBroadcaster, EndGameEventBroadcaster, PlayerChoseGodEventBroadcaster {
    private final GameRules rules;
    private final List<Player> players;
    private final int challengerPlayer;
    private int currentPlayer;
    private Worker currentWorker; // currentPlayer may only use one Worker during his turn
    private final Storage storage;
    private final Board board;
    private List<String> godPool;

    // Game state
    private GameState state;

    /**
     * Create a game object
     * @param players the players taking part of this game
     * @param rules the rules to follow
     */
    public Game(List<Player> players, GameRules rules) {
        this.rules = rules;
        this.players = new ArrayList<>();
        this.players.addAll(players);
        this.storage = new Storage(rules.getBlocks());
        this.board = new Board(rules.getBoardSize().getFirst(), rules.getBoardSize().getSecond(), rules.getBlocks().length - 1);
        this.challengerPlayer = IntStream.range(0, players.size()).boxed()
                .max(Comparator.comparing(i -> players.get(i).getGodLikeLevel())).orElse(-1);
        this.currentPlayer = challengerPlayer;

        if (rules.getPlayWithGods()) {
            this.state = new GodPoolSelectionState();
        } else {
            StartPlaying();
        }

    }

    public GameRules getRules() {
        return rules;
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

    /**
     * Assert that it's one player's turn
     * @param player the player
     * @throws InvalidCommandException if it's not the specified player's turn
     */
    private void checkTurn(Player player) throws InvalidCommandException {
        Player p = players.get(currentPlayer);
        if (!p.equals(player)) {
            throw new InvalidCommandException("It's not your turn");
        }
    }

    /**
     * Generates actions objects for each player and sets up to allow placing workers
     * To be called after setting god names for each player, or immediatly if playing with no gods
     */
    private void StartPlaying() {
        List<String> godNames = players.stream()
                .map(Player::getGodName)
                .collect(Collectors.toList());
        List<Actions> actions = GodFactory.makeActions(godNames);
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setActions(actions.get(i));
        }

        state = new WorkerPlacingState();
    }

    /**
     * Handle a set god-pool command
     * @param player the player performing this action
     * @param godPool the chosen god-pool
     * @throws InvalidCommandException if it's not this player's turn,
     *                                 if the player is not the challenger,
     *                                 if the god-pool has not been chosen yet,
     *                                 if the god-pool contains unknown god names,
     *                                 if the game proceeded past the god selection phase,
     */
    public void SetGodPool(Player player, List<String> godPool) throws InvalidCommandException {
        checkTurn(player);
        state.SetGodPool(player, godPool);
    }

    /**
     * Handle a set god command
     * @param player the player performing this action
     * @param god the chosen god
     * @throws InvalidCommandException if it's not this player's turn,
     *                                 if the player already chose a god,
     *                                 if the god name is unknown,
     *                                 if the game proceeded past the god selection phase
     */
    public void SetGod(Player player, String god) throws InvalidCommandException {
        checkTurn(player);
        state.SetGod(player, god);
    }

    /**
     * Handle a place worker command
     * @param player the player performing this action
     * @param x the x-axis coordinate on the board the worker will be placed at
     * @param y the y-axis coordinate on the board the worker will be placed at
     * @throws InvalidCommandException if it's not this player's turn,
     *                                 if the player has already placed down all its workers (depends on game rules),
     *                                 if x or y are invalid,
     *                                 if the tile at (x, y) is already occupied,
     *                                 if the game hasn't started playing (StartPlaying())
     */
    public void PlaceWorker(Player player, int x, int y) throws InvalidCommandException {
        checkTurn(player);
        state.PlaceWorker(player, x, y);
    }

    /**
     * Handle a move command
     * @param player the player that invokes this call
     * @param fromX the starting X coordinate on the board
     * @param fromY the starting Y coordinate on the board
     * @param toX the destination X coordinate on the board
     * @param toY the destination Y coordinate on the board
     * @throws InvalidCommandException if it's not this player's turn,
     *                                 if the starting or destination positions are invalid,
     *                                 if the game hasn't entered the playing phase (StartPlaying())
     * @throws InvalidMoveActionException if the move is invalid based on the player's god modifiers
     */
    public void Move(Player player, int fromX, int fromY, int toX, int toY) throws InvalidCommandException, InvalidMoveActionException {
        checkTurn(player);
        state.Move(player, fromX, fromY, toX, toY);
    }

    /**
     * Handle a build command
     * @param player the player that invokes this call
     * @param fromX the starting X coordinate on the board
     * @param fromY the starting Y coordinate on the board
     * @param toX the destination X coordinate on the board
     * @param toY the destination Y coordinate on the board
     * @throws InvalidCommandException if it's not this player's turn,
     *                                 if the starting or destination positions are invalid,
     *                                 if the game hasn't entered the playing phase (StartPlaying())
     * @throws InvalidBuildActionException if the build is invalid based on the player's god modifiers
     */
    public void Build(Player player, int fromX, int fromY, int toX, int toY, int lvl) throws InvalidCommandException, InvalidBuildActionException {
        checkTurn(player);
        state.Build(player, fromX, fromY, toX, toY, lvl);
    }

    /**
     * End the current turn
     * @param player the player that invokes this call
     * @param lose   if the player has lost and we're skipping to the next one
     * @return the next player to play
     * @throws InvalidCommandException if it's not this player's turn,
     *                                 if the player must complete other actions before ending the turn
     */
    public Player EndTurn(Player player, boolean lose) throws InvalidCommandException {
        checkTurn(player);

        int next;
        if (lose) {
            doLose(player); // removes the player from `List<Player> players`

            if (players.size() == 1) {
                Player winner = players.get(0);
                notifyEndGameEvent(new EndGameEventMessage(winner, rules.getEndGameTimer()));
                return winner;
            } else if (players.size() == 0) {
                // On usual execution this cannot happen. However, if debugging alone this is necessary
                notifyEndGameEvent(new EndGameEventMessage(null, rules.getEndGameTimer()));
                return player;  // better than null i guess
            }

            // The current player index points to the next player already,
            // unless i was the last player, then i need to cycle back to 0
            next = currentPlayer >= players.size() ? 0 : currentPlayer;
        } else {
            next = playerAfter(currentPlayer);
        }
        Player newPlayer = players.get(next);
        state.EndTurn(player /* previous */, newPlayer /* new */, lose);

        currentPlayer = next;  // only change the current player if state.EndTurn() did not throw
        notifyPlayerTurnUpdate(new PlayerTurnUpdateMessage(newPlayer));

        // Check if the new player has lost the game
        if (state.checkLose(newPlayer)) {
            return EndTurn(newPlayer, true);
        }

        return newPlayer;
    }

    /**
     * @param player the previous player
     * @return the index of the player after the one specified
     */
    private int playerAfter(int player) {
        int nextPlayer = player + 1;
        if (nextPlayer >= players.size()) {
            nextPlayer = 0;
        }
        return nextPlayer;
    }

    /**
     * @param player the player who performs this action
     * @param fromX the starting tile's x-coordinate
     * @param fromY the starting tile's y-coordinate
     * @param toX the destination tile's x-coordinate
     * @param toY the destination tile's y-coordinate
     * @return a pair containing the worker sitting on the starting tile, and the destination tile
     * @throws InvalidCommandException if the specified coordinates are invalid,
     *                                 if the starting tile is not hosting a worker
     *                                 if this player already performed an action with a different worker this same turn
     */
    private Pair<Worker, Tile> parseAction(Player player, int fromX, int fromY, int toX, int toY) throws InvalidCommandException {
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
        if (players.get(currentPlayer).equals(player) && currentWorker != null && !currentWorker.equals(w) || // if it's the players turn, check if he already used a worker
            !player.equals(w.getOwner())) {
            throw new InvalidCommandException("You cannot perform an action with the specified worker");
        }

        return new Pair<>(w, to);
    }

    /**
     * @param p the player
     * @return a pair containing two lists representing the available move actions and build actions for the player
     */
    public Pair<List<MoveCommandMessage>, List<BuildCommandMessage>> computeAvailableActions(Player p) {
        List<MoveCommandMessage> availMoves = new ArrayList<>();
        List<BuildCommandMessage> availBuilds = new ArrayList<>();

        for (int fromX = 0; fromX < board.getDimX(); fromX++) {
            for (int fromY = 0; fromY < board.getDimY(); fromY++) {
                for (int toX = 0; toX < board.getDimX(); toX++) {
                    for (int toY = 0; toY < board.getDimY(); toY++) {
                        try {
                            Pair<Worker, Tile> action = parseAction(p, fromX, fromY, toX, toY);
                            if (p.getActions().canMove() &&
                                    p.getActions().validMove(action.getFirst(), action.getSecond())) {
                                availMoves.add(new MoveCommandMessage(fromX, fromY, toX, toY));
                            }
                            for (int z = 0; z <= board.getMaxHeight(); z++) {
                                if (storage.getAvailable(z) > 0) {
                                    if (p.getActions().canBuild() &&
                                            p.getActions().validBuild(action.getFirst(), action.getSecond(), z)) {
                                        availBuilds.add(new BuildCommandMessage(fromX, fromY, toX, toY, z));
                                    }
                                }
                            }
                        } catch (Exception ignored) { } // It's perfectly fine to fail here
                    }
                }
            }
        }
        return new Pair<>(availMoves, availBuilds);
    }

    /**
     * @param player the player
     * @return a list of the player's workers on the board
     */
    public List<Worker> getWorkersOf(Player player) {
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

    /**
     * Remove a player from the game
     * @param player the player who has lost
     */
    private void doLose(Player player) {
        if (player.getActions() != null) {  // it may happen in debugging that a player loses before choosing a god
            player.getActions().onLose();
        }
        List<Worker> workers = getWorkersOf(player);
        for (Worker w : workers) {
            w.getTile().setOccupant(null);
            w.setTile(null);
        }
        players.remove(player);
        notifyPlayerLoseEvent(new PlayerLoseEventMessage(player));
    }

    //<editor-fold desc="Player turn update and lose event broadcasters">
    private final List<PlayerTurnUpdateListener> playerTurnUpdateListeners = new ArrayList<>();
    @Override
    public void addPlayerTurnUpdateListener(PlayerTurnUpdateListener listener) {
        synchronized (playerTurnUpdateListeners) {
            playerTurnUpdateListeners.add(listener);
        }
        onRegisterForPlayerTurnUpdate(listener);
    }
    @Override
    public void removePlayerTurnUpdateListener(PlayerTurnUpdateListener listener) {
        synchronized (playerTurnUpdateListeners) {
            playerTurnUpdateListeners.remove(listener);
        }
    }
    @Override
    public void notifyPlayerTurnUpdate(PlayerTurnUpdateMessage message) {
        synchronized (playerTurnUpdateListeners) {
            for (PlayerTurnUpdateListener listener : playerTurnUpdateListeners) {
                listener.onPlayerTurnUpdate(message);
            }
        }
    }
    @Override
    public void onRegisterForPlayerTurnUpdate(PlayerTurnUpdateListener listener) {
        listener.onPlayerTurnUpdate(new PlayerTurnUpdateMessage(players.get(currentPlayer)));
    }

    private final List<PlayerLoseEventListener> playerLoseEventListeners = new ArrayList<>();
    @Override
    public void addPlayerLoseEventListener(PlayerLoseEventListener listener) {
        synchronized (playerLoseEventListeners) {
            playerLoseEventListeners.add(listener);
        }
    }
    @Override
    public void removePlayerLoseEventListener(PlayerLoseEventListener listener) {
        synchronized (playerLoseEventListeners) {
            playerLoseEventListeners.remove(listener);
        }
    }
    @Override
    public void notifyPlayerLoseEvent(PlayerLoseEventMessage message) {
        synchronized (playerLoseEventListeners) {
            for (PlayerLoseEventListener listener : playerLoseEventListeners) {
                listener.onPlayerLoseEvent(message);
            }
        }
    }

    private final List<EndGameEventListener> endGameEventListeners = new ArrayList<>();
    @Override
    public void addEndGameEventListener(EndGameEventListener listener) {
        synchronized (endGameEventListeners) {
            endGameEventListeners.add(listener);
        }
    }
    @Override
    public void removeEndGameEventListener(EndGameEventListener listener) {
        synchronized (endGameEventListeners) {
            endGameEventListeners.remove(listener);
        }
    }
    @Override
    public void notifyEndGameEvent(EndGameEventMessage message) {
        synchronized (endGameEventListeners) {
            for (EndGameEventListener listener : endGameEventListeners) {
                listener.onEndGameEvent(message);
            }
        }
    }

    private final List<PlayerChoseGodEventListener> playerChoseGodEventListeners = new ArrayList<>();
    @Override
    public void addPlayerChoseGodEventListener(PlayerChoseGodEventListener listener) {
        synchronized (playerChoseGodEventListeners) {
            playerChoseGodEventListeners.add(listener);
        }
    }
    @Override
    public void removePlayerChoseGodEventListener(PlayerChoseGodEventListener listener) {
        synchronized (playerChoseGodEventListeners) {
            playerChoseGodEventListeners.remove(listener);
        }
    }
    @Override
    public void notifyPlayerChoseGodEvent(PlayerChoseGodEventMessage message) {
        synchronized (playerChoseGodEventListeners) {
            for (PlayerChoseGodEventListener listener : playerChoseGodEventListeners) {
                listener.onPlayerChoseGodEvent(message);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Game state pattern">
    public class GodPoolSelectionState implements GameState {
        /**
         * Handle setting a god-pool
         *
         * @param player the player performing this action
         * @param godPool the chosen god-pool
         * @throws InvalidCommandException if the player is not the challenger,
         *                                 if the god-pool contains unknown god names,
         */
        @Override
        public void SetGodPool(Player player, List<String> godPool) throws InvalidCommandException {
            Player challenger = players.get(challengerPlayer);
            if (!challenger.equals(player)) {
                throw new InvalidCommandException("You are not allowed to change the god pool");
            }
            if (godPool.size() != players.size() || !GodFactory.getGodNames().containsAll(godPool)) {
                throw new InvalidCommandException("Invalid god pool");
            }
            Game.this.godPool = new ArrayList<>();
            Game.this.godPool.addAll(godPool);
        }

        /**
         * Handle choosing a god
         * We can allow choosing a god without first setting a god-pool, if this player is playing alone
         *
         * @param player the player performing this action
         * @param god    the chosen god
         * @throws InvalidCommandException depending on implementation
         */
        @Override
        public void SetGod(Player player, String god) throws InvalidCommandException {
            if (players.size() > 1) {
                GameState.super.SetGod(player, god);    // call the default method, which will throw
            } else {
                List<String> godPool = new ArrayList<>();
                godPool.add(god);
                Game.this.SetGodPool(player, godPool);
                Game.this.EndTurn(player, false);   // changes the state so the next call will go to GodSelectionState
                Game.this.SetGod(player, god);
            }
        }

        /**
         * Handle turn change
         *
         * @param previousPlayer the player ending its turn
         * @param newPlayer the player beginning its turn
         * @param lose has the previous player lost
         * @throws InvalidCommandException if the previous player has not chosen a god-pool yet
         */
        @Override
        public void EndTurn(Player previousPlayer, Player newPlayer, boolean lose) throws InvalidCommandException {
            if (godPool == null) {
                throw new InvalidCommandException("You must choose a god pool");
            }
            state = new GodSelectionState();
        }
    }

    public class GodSelectionState implements GameState {
        /**
         * Handle choosing a god
         *
         * @param player the player performing this action
         * @param god the chosen god
         * @throws InvalidCommandException if the player already chose a god,
         *                                 if the god name is unknown
         */
        @Override
        public void SetGod(Player player, String god) throws InvalidCommandException {
            if (player.getGodName() != null) {
                throw new InvalidCommandException("You already chose a god. There's not turning back!");
            }
            if (!godPool.contains(god)) {
                throw new InvalidCommandException("Invalid god name");
            }
            godPool.remove(god);
            player.setGodName(god);
            notifyPlayerChoseGodEvent(new PlayerChoseGodEventMessage(player, GodFactory.godInfoFor(god)));
        }

        /**
         * Handle turn change
         *
         * @param previousPlayer the player ending its turn
         * @param newPlayer the player beginning its turn
         * @param lose has the previous player lost
         * @throws InvalidCommandException if the previous player has not chosen a god yet
         */
        @Override
        public void EndTurn(Player previousPlayer, Player newPlayer, boolean lose) throws InvalidCommandException {
             if (previousPlayer.getGodName() == null || previousPlayer.getGodName().isEmpty()) {
                 throw new InvalidCommandException("You must choose a god");
             }

            if (godPool.size() == 0) {
                // Everybody chose their god. Make Actions and move to next stage
                StartPlaying();
            }
        }
    }

    public class WorkerPlacingState implements GameState {
        /**
         * Handle placing down a worker
         *
         * @param player the player performing this action
         * @param x the x-axis coordinate on the board the worker will be placed at
         * @param y the y-axis coordinate on the board the worker will be placed at
         * @throws InvalidCommandException if the player has already placed down all its workers (depends on game rules),
         *                                 if x or y are invalid,
         *                                 if the tile at (x, y) is already occupied,
         */
        @Override
        public void PlaceWorker(Player player, int x, int y) throws InvalidCommandException {
            if (getWorkersOf(player).size() >= rules.getWorkers()) {
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

        /**
         * Handle turn change
         *
         * @param previousPlayer the player ending its turn
         * @param newPlayer the player beginning its turn
         * @param lose has the previous player lost
         * @throws InvalidCommandException if the previous player has not placed down all its workers yet
         */
        @Override
        public void EndTurn(Player previousPlayer, Player newPlayer, boolean lose) throws InvalidCommandException {
            if (getWorkersOf(previousPlayer).size() != rules.getWorkers()) {
                throw new InvalidCommandException("You must place down " + rules.getWorkers() + " workers");
            }

            int sumWorkers = players.stream().mapToInt(p -> getWorkersOf(p).size()).sum();
            if (sumWorkers == players.size() * rules.getWorkers()) {
                // Everybody placed down their workers. Let the game begin
                state = new PlayingState();
            }
        }
    }

    public class PlayingState implements GameState {
        /**
         * Handle a move command during playing phase
         *
         * @param player the player that invokes this call
         * @param fromX the starting X coordinate on the board
         * @param fromY the starting Y coordinate on the board
         * @param toX the destination X coordinate on the board
         * @param toY the destination Y coordinate on the board
         * @throws InvalidCommandException if the starting or destination positions are invalid
         * @throws InvalidMoveActionException if the move is invalid based on the player's god modifiers
         */
        @Override
        public void Move(Player player, int fromX, int fromY, int toX, int toY) throws InvalidCommandException, InvalidMoveActionException {
            Pair<Worker, Tile> action = parseAction(player, fromX, fromY, toX, toY);
            Worker w = action.getFirst();
            Tile to = action.getSecond();
            if (player.getActions().canMove() && player.getActions().validMove(w, to)) {
                currentWorker = w;
                boolean didWin = player.getActions().doMove(w, to);
                if (didWin) {
                    notifyEndGameEvent(new EndGameEventMessage(player /* winner */, rules.getEndGameTimer()));
                }
            } else {
                String errorMessage = "This player cannot move";
                if (player.getActions().canMove()) {
                    errorMessage += " to the desired position";
                }
                throw new InvalidMoveActionException(errorMessage);
            }

            if(checkLose(player)) {
                Game.this.EndTurn(player, true);
            }
        }

        /**
         * Handle a build command during playing phase
         *
         * @param player the player that invokes this call
         * @param fromX the starting X coordinate on the board
         * @param fromY the starting Y coordinate on the board
         * @param toX the destination X coordinate on the board
         * @param toY the destination Y coordinate on the board
         * @throws InvalidCommandException if the starting or destination positions are invalid
         * @throws InvalidBuildActionException if the build is invalid based on the player's god modifiers
         */
        @Override
        public void Build(Player player, int fromX, int fromY, int toX, int toY, int lvl) throws InvalidCommandException, InvalidBuildActionException {
            Pair<Worker, Tile> action = parseAction(player, fromX, fromY, toX, toY);
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

            if(checkLose(player)) {
                try {
                    Game.this.EndTurn(player, true);
                } catch (InvalidCommandException ignored) { } // failed to lose??
            }
        }

        /**
         * Handle turn change
         * @param previousPlayer the player ending its turn
         * @param newPlayer the player beginning its turn
         * @param lose has the previous player lost
         * @throws InvalidCommandException if the previous player has not completed his mandatory actions (based on god modifiers)
         */
        @Override
        public void EndTurn(Player previousPlayer, Player newPlayer, boolean lose) throws InvalidCommandException {
            if (!lose && (previousPlayer.getActions().mustMove() || previousPlayer.getActions().mustBuild())) {
                throw new InvalidCommandException("You must complete your actions first");
            }
            currentWorker = null;
            newPlayer.getActions().beginTurn();
        }

        /**
         * Check if the player should lose the game
         * @param player the player
         * @return true if the player should lose now
         */
        @Override
        public boolean checkLose(Player player) {
            Pair<List<MoveCommandMessage>, List<BuildCommandMessage>> actions = computeAvailableActions(player);
            if (player.getActions().mustMove()) {
                return actions.getFirst().isEmpty();
            }
            if (player.getActions().mustBuild()) {
                return actions.getSecond().isEmpty();
            }
            return false;
        }
    }
    //</editor-fold>
}
