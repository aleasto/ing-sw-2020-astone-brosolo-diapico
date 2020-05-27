package it.polimi.ingsw.Server;

import it.polimi.ingsw.Exceptions.InvalidBuildActionException;
import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Exceptions.InvalidMoveActionException;
import it.polimi.ingsw.Game.Actions.GodFactory;
import it.polimi.ingsw.Game.Game;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.Utils.SocketInfo;
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
    public static final long END_GAME_TIMER = 10 * 1000L; // 10s

    private final List<Player> players = new ArrayList<>();
    private final List<Player> spectators = new ArrayList<>();
    private final List<ServerRemoteView> remoteViews = new ArrayList<>();
    private Game game;
    private boolean gameEnded = false;

    public synchronized boolean isGameInProgress() {
        return game != null;
    }

    public synchronized void connect(SocketInfo client, Player player) {
        if (isGameInProgress()) {
            synchronized (spectators) {
                spectators.add(player);
            }
        } else {
            synchronized (players) {
                players.add(player);
            }
        }

        ServerRemoteView remoteView = new ServerRemoteView(client, player) {
            @Override
            public void onCommand(CommandMessage message) {
                if (gameEnded) {
                    this.onText(new TextMessage("Game has ended"));
                    return;
                }
                if (isGameInProgress() && !players.contains(getPlayer())) {
                    this.onText(new TextMessage("Spectators cannot issue commands"));
                }

                if (message instanceof MoveCommandMessage) {
                    gotMoveCommand(this, (MoveCommandMessage) message);
                } else if (message instanceof BuildCommandMessage) {
                    gotBuildCommand(this, (BuildCommandMessage) message);
                } else if (message instanceof EndTurnCommandMessage) {
                    gotEndTurnCommand(this, (EndTurnCommandMessage) message);
                } else if (message instanceof StartGameCommandMessage) {
                    gotStartGameCommand(this, (StartGameCommandMessage) message);
                } else if (message instanceof SetGodPoolCommandMessage) {
                    gotSetGodPoolMessage(this, (SetGodPoolCommandMessage) message);
                } else if (message instanceof SetGodCommandMessage) {
                    gotSetGodMessage(this, (SetGodCommandMessage) message);
                } else if (message instanceof PlaceWorkerCommandMessage) {
                    gotPlaceWorkerMessage(this, (PlaceWorkerCommandMessage) message);
                } else if (message instanceof SetSpectatorCommandMessage) {
                    gotSetSpectatorCommand(this, (SetSpectatorCommandMessage) message);
                }
            }

            @Override
            public void onDisconnect() {
                boolean wasSpectator = false;

                synchronized (spectators) {
                    if (spectators.contains(getPlayer())) {
                        wasSpectator = true;
                        spectators.remove(getPlayer());
                    }
                }

                synchronized (players) {
                    if (players.contains(getPlayer())) {
                        System.out.println("Player " + getPlayer().getName() + " disconnected");
                        players.remove(getPlayer());
                    }
                }

                synchronized (remoteViews) {
                    remoteViews.remove(this);
                    // Notify everyone that the players list has changed
                    for (View view : remoteViews) {
                        view.onPlayersUpdate(new PlayersUpdateMessage(players, spectators));
                    }
                }

                if (!wasSpectator && game != null && !gameEnded) {
                    game.notifyEndGameEvent(new EndGameEventMessage(null /* nobody won */, END_GAME_TIMER/1000));
                } else if (!isGameInProgress()) {
                    if (players.size() == 0) {
                        closeLobby();
                    }
                }

                onPlayerLeave(getPlayer());
            }
        };
        remoteView.startNetworkThread();
        synchronized (remoteViews) {
            remoteViews.add(remoteView);

            // Notify everyone that the players list has changed
            for (View view : remoteViews) {
                view.onPlayersUpdate(new PlayersUpdateMessage(players, spectators));
            }
        }

        if (isGameInProgress()) {
            addListeners(remoteView);
        }
        remoteView.onText(new TextMessage("Welcome!"));
    }

    public void addListeners(View view) {
        game.addPlayerTurnUpdateListener(view);
        game.addPlayerLoseEventListener(view);
        game.addEndGameEventListener(view);
        game.getBoard().addBoardUpdateListener(view);
        game.getStorage().addStorageUpdateListener(view);
    }

    public void removeListeners(View view) {
        game.removeEndGameEventListener(view);
        game.removePlayerLoseEventListener(view);
        game.removePlayerTurnUpdateListener(view);
        game.getBoard().removeBoardUpdateListener(view);
        game.getStorage().removeStorageUpdateListener(view);
    }

    public abstract void closeLobby();
    public abstract void onPlayerLeave(Player p);
    public abstract void onSpectatorModeChanged(Player p, boolean spectator);
    public abstract void onGameStart();

    public int getPlayerCount() {
        return players.size();
    }

    public int getSpectatorCount() {
        return spectators.size();
    }

    public void startGame() {
        System.out.println("Game started!");
        this.game = new Game(players);

        onGameStart();

        game.addEndGameEventListener(message -> {
            gameEnded = true;
            System.out.println("Game ended, lobby is closing in " + END_GAME_TIMER/1000 + " seconds");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (remoteViews) {
                        System.out.println("Disconnecting everyone...");
                        for (RemoteView view : remoteViews) {
                            removeListeners(view);
                            view.disconnect();
                        }
                    }
                    closeLobby();
                }
            }, END_GAME_TIMER);
        });

        game.addPlayerLoseEventListener(message -> {
            synchronized (players) {
                players.remove(message.getPlayer());
            }
            synchronized (spectators) {
                spectators.add(message.getPlayer());
            }
            synchronized (remoteViews) {
                for (RemoteView view : remoteViews) {
                    view.onPlayersUpdate(new PlayersUpdateMessage(players, spectators));
                }
            }
        });

        for (View view : remoteViews) {
            addListeners(view);

            if (view.getPlayer().equals(game.getCurrentPlayer())) {     // The current player is the challenger
                view.onText(new TextMessage("Choose a god pool of " + players.size()));
                view.onShowGods(new GodListMessage(GodFactory.getGodNames()));
            }
        }
    }

    private synchronized void gotSetSpectatorCommand(View view, SetSpectatorCommandMessage message) {
        if (isGameInProgress()) {
            view.onText(new TextMessage("You cannot change spectator mode while game is in progress"));
            return;
        }

        if (message.spectatorOn()) {
            synchronized (players) {
                players.remove(view.getPlayer());
            }
            synchronized (spectators) {
                spectators.add(view.getPlayer());
            }
        } else {
            synchronized (spectators) {
                spectators.remove(view.getPlayer());
            }
            synchronized (players) {
                players.add(view.getPlayer());
            }
        }

        for (View otherView : remoteViews) {
            otherView.onPlayersUpdate(new PlayersUpdateMessage(players, spectators));
        }
        onSpectatorModeChanged(view.getPlayer(), message.spectatorOn());
        view.onText(new TextMessage("Ok!"));
    }

    private synchronized void gotMoveCommand(View view, MoveCommandMessage message) {
        if (!isGameInProgress()) {
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.Move(view.getPlayer(), message.getFromX(), message.getFromY(), message.getToX(), message.getToY());
            if (!gameEnded)
                promptNextAction(view, "Ok! Next?");
        } catch (InvalidMoveActionException | InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private synchronized void gotBuildCommand(View view, BuildCommandMessage message) {
        if (!isGameInProgress()) {
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.Build(view.getPlayer(), message.getFromX(), message.getFromY(), message.getToX(), message.getToY(), message.getBlock());
            if (!gameEnded)
                promptNextAction(view, "Ok! Next?");
        } catch (InvalidBuildActionException | InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private synchronized void gotEndTurnCommand(View view, EndTurnCommandMessage message) {
        if (!isGameInProgress()) {
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            Player nextPlayer = game.EndTurn(view.getPlayer(), false);
            View nextPlayerView = remoteViews.stream().filter(v -> v.getPlayer().equals(nextPlayer)).collect(Collectors.toList()).get(0);
            view.onText(new TextMessage("Watch your enemies play"));
            promptNextAction(nextPlayerView, "It's your turn. What do you do?");
        } catch (InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
        } catch (NullPointerException e) {
            view.onText(new TextMessage("Game has not even started yet..."));
        }
    }

    private synchronized void gotStartGameCommand(View view, StartGameCommandMessage message) {
        if (!isGameInProgress()) {
            if (players.indexOf(view.getPlayer()) == 0) {
                startGame();
            } else {
                view.onText(new TextMessage("Only the lobby host may start the game"));
            }
        } else {
            view.onText(new TextMessage("Game is already in progress!"));
        }
    }

    private synchronized void gotSetGodPoolMessage(View view, SetGodPoolCommandMessage message) {
        if (!isGameInProgress()) {
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.SetGodPool(view.getPlayer(), message.getGodPool());
            for (View otherView : remoteViews) {
                otherView.onShowGods(new GodListMessage(game.getGodPool()));
            }
            Player nextPlayer = game.EndTurn(view.getPlayer(), false);
            View nextPlayerView = remoteViews.stream().filter(v -> v.getPlayer().equals(nextPlayer)).collect(Collectors.toList()).get(0);
            nextPlayerView.onText(new TextMessage("Choose a god from the pool"));
            view.onText(new TextMessage("Ok! Others are choosing their god..."));
        } catch (InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private synchronized void gotSetGodMessage(View view, SetGodCommandMessage message) {
        if (!isGameInProgress()) {
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.SetGod(view.getPlayer(), message.getGodName());
            Player nextPlayer = game.EndTurn(view.getPlayer(), false);
            View nextPlayerView = remoteViews.stream().filter(v -> v.getPlayer().equals(nextPlayer)).collect(Collectors.toList()).get(0);

            if (game.getGodPool() != null && game.getGodPool().size() == 0) {
                for (View otherView : remoteViews) {
                    if (otherView != nextPlayerView) {
                        otherView.onText(new TextMessage("All set. Others are placing down their workers"));
                    } else {
                        otherView.onText(new TextMessage("All set. It's your turn to place down workers"));
                    }
                    otherView.onShowGods(new GodListMessage(null));
                }
            } else {
                for (View otherView : remoteViews) {
                    otherView.onShowGods(new GodListMessage(game.getGodPool()));
                }
                nextPlayerView.onText(new TextMessage("Choose a god from the pool"));
                view.onText(new TextMessage("Ok! Others are choosing their god..."));
            }
        } catch (InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private synchronized void gotPlaceWorkerMessage(View view, PlaceWorkerCommandMessage message) {
        if (!isGameInProgress()) {
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.PlaceWorker(view.getPlayer(), message.getX(), message.getY());
            view.onText(new TextMessage("Ok!"));
        } catch (InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private void promptNextAction(View view, String message) {
        view.onText(new TextMessage(message));
        if (game != null) {
            Pair<List<MoveCommandMessage>, List<BuildCommandMessage>> nextActions = game.computeAvailableActions(view.getPlayer());
            view.onNextActionsUpdate(new NextActionsUpdateMessage(nextActions.getFirst(), nextActions.getSecond()));
        }
    }
}
