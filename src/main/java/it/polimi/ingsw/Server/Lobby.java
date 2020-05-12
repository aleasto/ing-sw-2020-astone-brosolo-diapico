package it.polimi.ingsw.Server;

import it.polimi.ingsw.Exceptions.InvalidBuildActionException;
import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Exceptions.InvalidMoveActionException;
import it.polimi.ingsw.Game.Actions.GodFactory;
import it.polimi.ingsw.Game.Game;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Worker;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.ServerRemoteView;
import it.polimi.ingsw.View.View;
import org.w3c.dom.Text;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Lobby {
    List<Player> players = new ArrayList<Player>();
    List<ServerRemoteView> remoteViews = new ArrayList<ServerRemoteView>();
    Game game;

    public void connect(Socket client, Player player) {
        players.add(player);
        ServerRemoteView remoteView = new ServerRemoteView(client, player);
        new Thread(remoteView).start();
        registerView(remoteView);
        remoteViews.add(remoteView);

        // Notify everyone that the players list has changed
        for (View view : remoteViews) {
            view.onPlayersUpdate(new PlayersUpdateMessage(players));
        }
    }

    public void startGame() {
        System.out.println("Game started!");
        this.game = new Game(players);

        for (View view : remoteViews) {
            game.getBoard().addBoardUpdateListener(view);
            game.getStorage().addStorageUpdateListener(view);
            game.addPlayerTurnUpdateListener(view);

            if (view.getPlayer().equals(game.getCurrentPlayer())) {     // The current player is the challenger
                view.onText(new TextMessage("Choose a god pool of " + players.size()));
                view.onShowGods(new GodListMessage(GodFactory.getGodNames()));
            }
        }
    }

    private void gotMoveCommand(View view, MoveCommandMessage message) {
        if (game == null) {
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.Move(view.getPlayer(), message.getFromX(), message.getFromY(), message.getToX(), message.getToY());
            promptNextAction(view, "Ok! Next?");
        } catch (InvalidMoveActionException | InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private void gotBuildCommand(View view, BuildCommandMessage message) {
        if (game == null) {
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.Build(view.getPlayer(), message.getFromX(), message.getFromY(), message.getToX(), message.getToY(), message.getBlock());
            promptNextAction(view, "Ok! Next?");
        } catch (InvalidBuildActionException | InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private void gotEndTurnCommand(View view, EndTurnCommandMessage message) {
        if (game == null) {
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            Player nextPlayer = game.EndTurn(view.getPlayer());
            View nextPlayerView = remoteViews.stream().filter(v -> v.getPlayer().equals(nextPlayer)).collect(Collectors.toList()).get(0);
            promptNextAction(nextPlayerView, "It's your turn. What do you do?");
            promptNextAction(view, "Watch your enemies play");
        } catch (InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
        } catch (NullPointerException e) {
            view.onText(new TextMessage("Game has not even started yet..."));
        }
    }

    private void gotStartGameCommand(View view, StartGameCommandMessage message) {
        if (this.game == null) {
            if (players.indexOf(view.getPlayer()) == 0) {
                startGame();
            } else {
                view.onText(new TextMessage("Only the lobby creator may start the game"));
            }
        } else {
            view.onText(new TextMessage("Game is already in progress!"));
        }
    }

    private void gotSetGodPoolMessage(View view, SetGodPoolCommandMessage message) {
        if (game == null) {
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.SetGodPool(view.getPlayer(), message.getGodPool());
            for (View otherView : remoteViews) {
                otherView.onShowGods(new GodListMessage(game.getGodPool()));
            }
            Player nextPlayer = game.EndTurn(view.getPlayer());
            View nextPlayerView = remoteViews.stream().filter(v -> v.getPlayer().equals(nextPlayer)).collect(Collectors.toList()).get(0);
            nextPlayerView.onText(new TextMessage("Choose a god from the pool"));
            view.onText(new TextMessage("Ok! Others are choosing their god..."));
        } catch (InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private void gotSetGodMessage(View view, SetGodCommandMessage message) {
        if (game == null) {
            view.onText(new TextMessage("Game has not even started yet..."));
            return;
        }

        try {
            game.SetGod(view.getPlayer(), message.getGodName());
            Player nextPlayer = game.EndTurn(view.getPlayer());
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

    private void gotPlaceWorkerMessage(View view, PlaceWorkerCommandMessage message) {
        try {
            game.PlaceWorker(view.getPlayer(), message.getX(), message.getY());
            view.onText(new TextMessage("Ok!"));
        } catch (InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private void registerView(View view) {
        view.addCommandListener((CommandMessage message) -> {
            if (message instanceof MoveCommandMessage) {
                gotMoveCommand(view, (MoveCommandMessage) message);
            } else if (message instanceof BuildCommandMessage) {
                gotBuildCommand(view, (BuildCommandMessage) message);
            } else if (message instanceof EndTurnCommandMessage) {
                gotEndTurnCommand(view, (EndTurnCommandMessage) message);
            } else if (message instanceof StartGameCommandMessage) {
                gotStartGameCommand(view, (StartGameCommandMessage) message);
            } else if (message instanceof SetGodPoolCommandMessage) {
                gotSetGodPoolMessage(view, (SetGodPoolCommandMessage) message);
            } else if (message instanceof SetGodCommandMessage) {
                gotSetGodMessage(view, (SetGodCommandMessage) message);
            } else if (message instanceof PlaceWorkerCommandMessage) {
                gotPlaceWorkerMessage(view, (PlaceWorkerCommandMessage) message);
            }
        });

        promptNextAction(view, "Welcome!");
    }

    private void promptNextAction(View view, String message) {
        view.onText(new TextMessage(message));
        if (game != null) {
            Pair<List<MoveCommandMessage>, List<BuildCommandMessage>> nextActions = game.computeAvailableActions(view.getPlayer());
            view.onNextActionsUpdate(new NextActionsUpdateMessage(nextActions.getFirst(), nextActions.getSecond()));
        }
    }
}
