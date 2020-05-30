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

    public Game(List<Player> players, GameRules rules) {
        this.rules = rules;
        this.players = new ArrayList<Player>();
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

    private void checkTurn(Player player) throws InvalidCommandException {
        Player p = players.get(currentPlayer);
        if (!p.equals(player)) {
            throw new InvalidCommandException("It's not your turn");
        }
    }

    public void StartPlaying() {
        List<String> godNames = players.stream()
                .map(Player::getGodName)
                .collect(Collectors.toList());
        List<Actions> actions = GodFactory.makeActions(godNames);
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setActions(actions.get(i));
        }

        state = new WorkerPlacingState();
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
        checkTurn(player);
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
     * @param lose   if the player has lost and we're skipping to the next one
     * @return the next player to play
     */
    public Player EndTurn(Player player, boolean lose) throws InvalidCommandException {
        checkTurn(player);

        int next;
        if (lose) {
            doLose(player); // removes the player from `List<Player> players`

            if (players.size() == 1) {
                Player winner = players.get(0);
                notifyEndGameEvent(new EndGameEventMessage(winner, Lobby.END_GAME_TIMER/1000));
                return winner;
            } else if (players.size() == 0) {
                // On usual execution this cannot happen. However, if debugging alone this is necessary
                notifyEndGameEvent(new EndGameEventMessage(null, Lobby.END_GAME_TIMER/1000));
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
     * Get the index of the next player
     * @param player the current player
     * @return the next player
     */
    private int playerAfter(int player) {
        int nextPlayer = player + 1;
        if (nextPlayer >= players.size()) {
            nextPlayer = 0;
        }
        return nextPlayer;
    }

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
        public void EndTurn(Player previousPlayer, Player newPlayer, boolean lose) throws InvalidCommandException {
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
            notifyPlayerChoseGodEvent(new PlayerChoseGodEventMessage(p, god));
        }

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

        @Override
        public void EndTurn(Player previousPlayer, Player newPlayer, boolean lose) throws InvalidCommandException {
            if (getWorkersOf(previousPlayer).size() != rules.getWorkers()) {
                throw new InvalidCommandException("You must place down 2 workers");
            }

            int sumWorkers = players.stream().mapToInt(p -> getWorkersOf(p).size()).sum();
            if (sumWorkers == players.size() * rules.getWorkers()) {
                // Everybody placed down their workers. Let the game begin
                state = new PlayingState();
            }
        }
    }

    public class PlayingState implements GameState {
        @Override
        public void Move(Player player, int fromX, int fromY, int toX, int toY) throws InvalidCommandException, InvalidMoveActionException {
            Pair<Worker, Tile> action = parseAction(player, fromX, fromY, toX, toY);
            Worker w = action.getFirst();
            Tile to = action.getSecond();
            if (player.getActions().canMove() && player.getActions().validMove(w, to)) {
                currentWorker = w;
                boolean didWin = player.getActions().doMove(w, to);
                if (didWin) {
                    notifyEndGameEvent(new EndGameEventMessage(player /* winner */, Lobby.END_GAME_TIMER/1000));
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

        @Override
        public void EndTurn(Player previousPlayer, Player newPlayer, boolean lose) throws InvalidCommandException {
            if (!lose && (previousPlayer.getActions().mustMove() || previousPlayer.getActions().mustBuild())) {
                throw new InvalidCommandException("You must complete your actions first");
            }
            currentWorker = null;
            newPlayer.getActions().beginTurn();
        }

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
