package it.polimi.ingsw.Server;

import it.polimi.ingsw.Exceptions.InvalidBuildActionException;
import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Exceptions.InvalidMoveActionException;
import it.polimi.ingsw.Game.Actions.GodFactory;
import it.polimi.ingsw.Game.Game;
import it.polimi.ingsw.Game.GameRules;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Utils.ConfReader;
import it.polimi.ingsw.Utils.Log;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.RemoteView;
import it.polimi.ingsw.View.ServerRemoteView;
import it.polimi.ingsw.View.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public abstract class Lobby {
    private static final int DEFAULT_MIN_PLAYERS = 2;
    private static final int DEFAULT_MAX_PLAYERS = 4;

    private final ConfReader confReader;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> spectators = new ArrayList<>();
    private final List<ServerRemoteView> remoteViews = new ArrayList<>();
    private Game game;
    private boolean gameEnded = false;

    protected Lobby(ConfReader confReader) {
        this.confReader = confReader;
    }

    public synchronized boolean isGameInProgress() {
        return game != null;
    }

    public synchronized Game getGame() {
        return game;
    }

    /**
     * Connect a client to this lobby.
     * Start listening to commands.
     *
     * @param remoteView the client's remote view
     * @param player the client's created player object
     */
    public synchronized void connect(ServerRemoteView remoteView, Player player) {
        remoteView.setPlayer(player);

        if (isGameInProgress() || getPlayerCount() >= confReader.getInt("max_players", DEFAULT_MAX_PLAYERS)) {
            spectators.add(player);
        } else {
            players.add(player);
        }

        remoteView.setCommandListener(message -> {
            synchronized (Lobby.this) {
                if (gameEnded) {
                    Log.logInvalidAction(player, message.toString(), "game has ended");
                    remoteView.onText(new TextMessage("Game has ended"));
                    return;
                }
                if (isGameInProgress() && !players.contains(player)) {
                    Log.logInvalidAction(player, message.toString(), "spectators cannot issue commands");
                    remoteView.onText(new TextMessage("Spectators cannot issue commands"));
                }

                if (message instanceof MoveCommandMessage) {
                    gotMoveCommand(remoteView, (MoveCommandMessage) message);
                } else if (message instanceof BuildCommandMessage) {
                    gotBuildCommand(remoteView, (BuildCommandMessage) message);
                } else if (message instanceof EndTurnCommandMessage) {
                    gotEndTurnCommand(remoteView, (EndTurnCommandMessage) message);
                } else if (message instanceof StartGameCommandMessage) {
                    gotStartGameCommand(remoteView, (StartGameCommandMessage) message);
                } else if (message instanceof SetGodPoolCommandMessage) {
                    gotSetGodPoolMessage(remoteView, (SetGodPoolCommandMessage) message);
                } else if (message instanceof SetGodCommandMessage) {
                    gotSetGodMessage(remoteView, (SetGodCommandMessage) message);
                } else if (message instanceof PlaceWorkerCommandMessage) {
                    gotPlaceWorkerMessage(remoteView, (PlaceWorkerCommandMessage) message);
                } else if (message instanceof SetSpectatorCommandMessage) {
                    gotSetSpectatorCommand(remoteView, (SetSpectatorCommandMessage) message);
                }
            }
        });
        remoteView.setDisconnectListener(() -> {
            synchronized (Lobby.this) {
                boolean wasSpectator = false;

                if (spectators.contains(player)) {
                    wasSpectator = true;
                    spectators.remove(player);
                    if (isGameInProgress()) {
                        removeListeners(remoteView);
                    }
                    Log.logPlayerAction(player, "disconnected as spectator");
                } else if (players.contains(player)) {
                    players.remove(player);
                    if (isGameInProgress()) {
                        removeListeners(remoteView);
                    }
                    Log.logPlayerAction(player, "disconnected");
                }

                remoteViews.remove(remoteView);
                // Notify everyone that the players list has changed
                for (View view : remoteViews) {
                    view.onPlayersUpdate(new PlayersUpdateMessage(players, spectators));
                }

                if (!wasSpectator && game != null && !gameEnded) {
                    game.notifyEndGameEvent(new EndGameEventMessage(null /* nobody won */, game.getRules().getEndGameTimer()));
                } else if (!isGameInProgress()) {
                    if (players.size() == 0) {
                        closeLobby();
                    }
                }

                onPlayerLeave(player);
            }
        });

        remoteViews.add(remoteView);

        // Notify everyone that the players list has changed
        for (View view : remoteViews) {
            view.onPlayersUpdate(new PlayersUpdateMessage(players, spectators));
        }

        if (isGameInProgress()) {
            addListeners(remoteView);
        }
        remoteView.onText(new TextMessage("Welcome!"));
    }

    /**
     * Add a view to all game event dispatchers
     *
     * @param view a client's view
     */
    public synchronized void addListeners(View view) {
        game.addPlayerTurnUpdateListener(view);
        game.addPlayerLoseEventListener(view);
        game.addEndGameEventListener(view);
        game.addPlayerChoseGodEventListener(view);
        game.getBoard().addBoardUpdateListener(view);
        game.getStorage().addStorageUpdateListener(view);
    }

    /**
     * Remove a view from all game event dispatchers
     *
     * @param view a client's view
     */
    public synchronized void removeListeners(View view) {
        game.removeEndGameEventListener(view);
        game.removePlayerLoseEventListener(view);
        game.removePlayerTurnUpdateListener(view);
        game.removePlayerChoseGodEventListener(view);
        game.getBoard().removeBoardUpdateListener(view);
        game.getStorage().removeStorageUpdateListener(view);
    }

    public abstract void closeLobby();
    public abstract void onPlayerLeave(Player p);
    public abstract void onSpectatorModeChanged(Player p, boolean spectator);
    public abstract void onGameStart(List<Player> players);

    public synchronized int getPlayerCount() {
        return players.size();
    }

    public synchronized int getSpectatorCount() {
        return spectators.size();
    }

    /**
     * Start a game with all connected players
     *
     * @param rules the rules to follow
     */
    public synchronized void startGame(GameRules rules) {
        rules.fillDefaults(confReader);
        this.game = new Game(players, rules);
        onGameStart(players);

        game.addEndGameEventListener(message -> {
            synchronized (Lobby.this) {
                gameEnded = true;
                if (message.getWinner() != null)
                    Log.logPlayerAction(message.getWinner(), "won the game");
                System.out.println("Game ended, lobby is closing in " + game.getRules().getEndGameTimer() + " seconds");
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (Lobby.this) {
                            System.out.println("Disconnecting everyone...");
                            for (RemoteView view : remoteViews) {
                                removeListeners(view);
                                view.disconnect();
                            }
                            closeLobby();
                        }
                    }
                }, rules.getEndGameTimer() * 1000L);
            }
        });

        game.addPlayerLoseEventListener(message -> {
            synchronized (Lobby.this) {
                Log.logPlayerAction(message.getPlayer(), "lost the game and became spectator");
                players.remove(message.getPlayer());
                spectators.add(message.getPlayer());
                for (RemoteView view : remoteViews) {
                    view.onPlayersUpdate(new PlayersUpdateMessage(players, spectators));
                }
            }
        });

        for (View view : remoteViews) {
            addListeners(view);

            if (view.getPlayer().equals(game.getCurrentPlayer())) {     // The current player is the challenger
                if (game.getRules().getPlayWithGods()) {
                    view.onText(new TextMessage("Choose a god pool of " + players.size()));
                    view.onShowGods(new GodListMessage(GodFactory.getGodInfo(), players.size()));
                } else {
                    view.onText(new TextMessage("It's your turn to place down " +
                            game.getRules().getWorkers() + " workers"));
                }
            }
        }

        if (!game.getRules().getPlayWithGods()) {
            for (View view : remoteViews) {
                view.onShowGods(new GodListMessage(null, 0));
            }
        }
    }

    /**
     * Get the view associated to a player
     *
     * @param p the player
     * @return the view associated to the player so that view.getPlayer.equals(player)
     */
    private View getViewFor(Player p) {
        return remoteViews.stream().filter(v -> v.getPlayer().equals(p)).collect(Collectors.toList()).get(0);
    }

    private synchronized void gotSetSpectatorCommand(View view, SetSpectatorCommandMessage message) {
        if (isGameInProgress()) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), "game already in progress");
            view.onText(new TextMessage("You cannot change spectator mode while game is in progress"));
            return;
        }

        if (message.spectatorOn() && spectators.contains(view.getPlayer()) ||
            !message.spectatorOn() && players.contains(view.getPlayer())) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), "was already in same spectator mode");
            view.onText(new TextMessage("Was already " + (message.spectatorOn() ? "spectator" : "playing")));
            return;
        }

        if (!message.spectatorOn() && getPlayerCount() >= confReader.getInt("max_players", DEFAULT_MAX_PLAYERS)) {
            Log.logInvalidAction(view.getPlayer(), message.toString(),
                    "the max player number has been reached");
            view.onText(new TextMessage("You cannot cannot play because the max player number has been reached"));
            return;
        }

        if (message.spectatorOn()) {
            players.remove(view.getPlayer());
            spectators.add(view.getPlayer());
        } else {
            spectators.remove(view.getPlayer());
            players.add(view.getPlayer());
        }
        Log.logPlayerAction(view.getPlayer(), message.toString());

        for (View otherView : remoteViews) {
            otherView.onPlayersUpdate(new PlayersUpdateMessage(players, spectators));
        }
        onSpectatorModeChanged(view.getPlayer(), message.spectatorOn());
        view.onText(new TextMessage("Ok!"));
    }

    private synchronized void gotMoveCommand(View view, MoveCommandMessage message) {
        if (!isGameInProgress()) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), "game has not started");
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.Move(view.getPlayer(), message.getFromX(), message.getFromY(), message.getToX(), message.getToY());
            Log.logPlayerAction(view.getPlayer(), message.toString());
            if (!gameEnded)
                promptNextAction(view, "Ok! Next?");
        } catch (InvalidMoveActionException | InvalidCommandException e) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), e.getMessage());
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private synchronized void gotBuildCommand(View view, BuildCommandMessage message) {
        if (!isGameInProgress()) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), "game has not started");
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.Build(view.getPlayer(), message.getFromX(), message.getFromY(), message.getToX(), message.getToY(), message.getBlock());
            Log.logPlayerAction(view.getPlayer(), message.toString());
            if (!gameEnded)
                promptNextAction(view, "Ok! Next?");
        } catch (InvalidBuildActionException | InvalidCommandException e) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), e.getMessage());
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private synchronized void gotEndTurnCommand(View view, EndTurnCommandMessage message) {
        if (!isGameInProgress()) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), "game has not started");
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            Player nextPlayer = game.EndTurn(view.getPlayer(), false);
            Log.logPlayerAction(view.getPlayer(), message.toString());
            if (!gameEnded) {
                // If ending the turn did not cause the game to end
                View nextPlayerView = getViewFor(nextPlayer);
                view.onText(new TextMessage("Watch your enemies play"));
                promptNextAction(nextPlayerView, "It's your turn. What do you do?");
            }
        } catch (InvalidCommandException e) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), e.getMessage());
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private synchronized void gotStartGameCommand(View view, StartGameCommandMessage message) {
        if (!isGameInProgress()) {
            if (getPlayerCount() < confReader.getInt("min_players", DEFAULT_MIN_PLAYERS)) {
                Log.logInvalidAction(view.getPlayer(), message.toString(), "not enough players");
                view.onText(new TextMessage("Not enough players"));
                return;
            }

            if (players.indexOf(view.getPlayer()) == 0) {
                Log.logPlayerAction(view.getPlayer(), message.toString());
                startGame(message.getRules());
            } else {
                Log.logInvalidAction(view.getPlayer(), message.toString(), "Player is not host");
                view.onText(new TextMessage("Only the lobby host may start the game"));
            }
        } else {
            Log.logInvalidAction(view.getPlayer(), message.toString(), "game is already in progress");
            view.onText(new TextMessage("Game is already in progress!"));
        }
    }

    private synchronized void gotSetGodPoolMessage(View view, SetGodPoolCommandMessage message) {
        if (!isGameInProgress()) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), "game has not started");
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.SetGodPool(view.getPlayer(), message.getGodPool());
            Log.logPlayerAction(view.getPlayer(), message.toString());
            Player nextPlayer = game.EndTurn(view.getPlayer(), false);
            for (View otherView : remoteViews) {
                otherView.onShowGods(new GodListMessage(GodFactory.godInfosFor(game.getGodPool()), 1));
            }
            View nextPlayerView = getViewFor(nextPlayer);
            nextPlayerView.onText(new TextMessage("Choose a god from the pool"));
            view.onText(new TextMessage("Ok! Others are choosing their god..."));
        } catch (InvalidCommandException e) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), e.getMessage());
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private synchronized void gotSetGodMessage(View view, SetGodCommandMessage message) {
        if (!isGameInProgress()) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), "game has not started");
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.SetGod(view.getPlayer(), message.getGodName());
            Log.logPlayerAction(view.getPlayer(), message.toString());
            Player nextPlayer = game.EndTurn(view.getPlayer(), false);
            View nextPlayerView = getViewFor(nextPlayer);

            if (game.getGodPool() != null && game.getGodPool().size() == 0) {
                for (View otherView : remoteViews) {
                    if (otherView != nextPlayerView) {
                        otherView.onText(new TextMessage("All set. Others are placing down their workers"));
                    } else {
                        otherView.onText(new TextMessage("All set. It's your turn to place down "
                                + game.getRules().getWorkers() + " workers"));
                    }
                    otherView.onShowGods(new GodListMessage(null, 0));
                }
            } else {
                for (View otherView : remoteViews) {
                    otherView.onShowGods(new GodListMessage(GodFactory.godInfosFor(game.getGodPool()), 1));
                }
                nextPlayerView.onText(new TextMessage("Choose a god from the pool"));
                view.onText(new TextMessage("Ok! Others are choosing their god..."));
            }
        } catch (InvalidCommandException e) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), e.getMessage());
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private synchronized void gotPlaceWorkerMessage(View view, PlaceWorkerCommandMessage message) {
        if (!isGameInProgress()) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), "game has not started");
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.PlaceWorker(view.getPlayer(), message.getX(), message.getY());
            Log.logPlayerAction(view.getPlayer(), message.toString());
            if (game.getWorkersOf(view.getPlayer()).size() == game.getRules().getWorkers()) {
                Player nextPlayer = game.EndTurn(view.getPlayer(), false);
                view.onText(new TextMessage("Watch your enemies play"));
                View nextPlayerView = getViewFor(nextPlayer);
                int sumWorkers = players.stream().mapToInt(p -> game.getWorkersOf(p).size()).sum();
                if (sumWorkers == players.size() * game.getRules().getWorkers()) {
                    promptNextAction(nextPlayerView, "Let the games begin!");
                } else {
                    nextPlayerView.onText(new TextMessage("It's your turn to place down " +
                            game.getRules().getWorkers() + " workers"));
                }
            } else {
                view.onText(new TextMessage("Ok!"));
            }
        } catch (InvalidCommandException e) {
            Log.logInvalidAction(view.getPlayer(), message.toString(), e.getMessage());
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    /**
     * Send a player the list of next available actions and a textual hint
     *
     * @param view the player's view
     * @param message the textual hint
     */
    private void promptNextAction(View view, String message) {
        view.onText(new TextMessage(message));
        if (game != null) {
            Pair<List<MoveCommandMessage>, List<BuildCommandMessage>> nextActions = game.computeAvailableActions(view.getPlayer());
            view.onNextActionsUpdate(new NextActionsUpdateMessage(nextActions.getFirst(), nextActions.getSecond(),
                    view.getPlayer().getActions().mustMove(), view.getPlayer().getActions().mustBuild()));
        }
    }
}
