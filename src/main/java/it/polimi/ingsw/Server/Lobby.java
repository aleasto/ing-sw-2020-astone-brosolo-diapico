package it.polimi.ingsw.Server;

import it.polimi.ingsw.Exceptions.InvalidBuildActionException;
import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Exceptions.InvalidMoveActionException;
import it.polimi.ingsw.Game.Game;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Worker;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.View.Communication.*;
import it.polimi.ingsw.View.ServerRemoteView;
import it.polimi.ingsw.View.View;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        System.out.println("Client entered lobby");

        // Notify everyone that the players list has changed
        for (View view : remoteViews) {
            view.onPlayersUpdate(new PlayersUpdateMessage(players));
        }
    }

    public void startGame() {
        System.out.println("Game started!");
        this.game = new Game(players);

        // Just for debugging purposes, let's spawn in some Workers
        List<Pair<Integer, Integer>> spots = new ArrayList();
        spots.add(new Pair(0, 0));
        spots.add(new Pair(0, 1));
        spots.add(new Pair(4, 4));
        spots.add(new Pair(4, 3));
        for (Player p : players) {
            Pair<Integer, Integer> spot = spots.remove(0);
            new Worker(p, game.getBoard().getAt(spot.getFirst(), spot.getSecond()));
            spot = spots.remove(0);
            new Worker(p, game.getBoard().getAt(spot.getFirst(), spot.getSecond()));
        }

        for (View view : remoteViews) {
            game.getBoard().addBoardUpdateListener(view);
            game.getStorage().addStorageUpdateListener(view);
        }
    }

    private void gotMoveCommand(View view, MoveCommandMessage message) {
        try {
            game.Move(view.getPlayer(), message.getFromX(), message.getFromY(), message.getToX(), message.getToY());
            promptNextAction(view, "Ok! Next?");
        } catch (InvalidMoveActionException | InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private void gotBuildCommand(View view, BuildCommandMessage message) {
        try {
            game.Build(view.getPlayer(), message.getFromX(), message.getFromY(), message.getToX(), message.getToY(), message.getBlock());
            promptNextAction(view, "Ok! Next?");
        } catch (InvalidBuildActionException | InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
        }
    }

    private void gotEndTurnCommand(View view, EndTurnCommandMessage message) {
        try {
            Player nextPlayer = game.EndTurn(view.getPlayer());
            View nextPlayerView = remoteViews.stream().filter(v -> v.getPlayer().equals(nextPlayer)).collect(Collectors.toList()).get(0);
            promptNextAction(nextPlayerView, "It's your turn. What do you do?");
            promptNextAction(view, "Watch your enemies play");
        } catch (InvalidCommandException e) {
            view.onText(new TextMessage(e.getMessage()));
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
