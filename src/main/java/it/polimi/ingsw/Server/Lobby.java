package it.polimi.ingsw.Server;

import it.polimi.ingsw.Exceptions.InvalidBuildActionException;
import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Exceptions.InvalidMoveActionException;
import it.polimi.ingsw.Game.Game;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Worker;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.View.Comunication.*;
import it.polimi.ingsw.View.ServerRemoteView;
import it.polimi.ingsw.View.View;
import org.w3c.dom.Text;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Lobby {
    List<Player> players = new ArrayList<Player>();
    List<ServerRemoteView> remoteViews = new ArrayList<ServerRemoteView>();
    Game game;

    public void connect(Socket client, Player player) {
        players.add(player);
        remoteViews.add(new ServerRemoteView(client, player));

        System.out.println("Client entered lobby");

        // TODO: Change this...
        if (players.size() == 2) {
            startGame();
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

        for (ServerRemoteView view : remoteViews) {
            new Thread(view).start();
            registerView(view);
        }
    }

    private void registerView(View view) {
        view.addMoveCommandListener((MoveCommandMessage message) -> {
            try {
                game.Move(view.getPlayer(), message.getFromX(), message.getFromY(), message.getToX(), message.getToY());
                promptNextAction(view, "Ok! Next?");
            } catch (InvalidMoveActionException | InvalidCommandException e) {
                view.onText(new TextMessage(e.getMessage()));
            }
        });
        view.addBuildCommandListener((BuildCommandMessage message) -> {
            // Perform the action and notify a response
            try {
                game.Build(view.getPlayer(), message.getFromX(), message.getFromY(), message.getToX(), message.getToY(), message.getBlock());
                promptNextAction(view, "Ok! Next?");
            } catch (InvalidBuildActionException | InvalidCommandException e) {
                view.onText(new TextMessage(e.getMessage()));
            }
        });
        view.addEndTurnCommandListener((EndTurnCommandMessage message) -> {
            try {
                Player nextPlayer = game.EndTurn(view.getPlayer());
                View nextPlayerView = remoteViews.stream().filter(v -> v.getPlayer().equals(nextPlayer)).collect(Collectors.toList()).get(0);
                promptNextAction(nextPlayerView, "It's your turn. What do you do?");
                promptNextAction(view, "Watch your enemies play");
            } catch (InvalidCommandException e) {
                view.onText(new TextMessage(e.getMessage()));
            }
        });

        promptNextAction(view, "Welcome!");
        game.getBoard().addBoardUpdateListener(view);
        game.getStorage().addStorageUpdateListener(view);
    }

    private void promptNextAction(View view, String message) {
        Pair<List<MoveCommandMessage>, List<BuildCommandMessage>> nextActions = game.computeAvailableActions(view.getPlayer());
        view.onText(new TextMessage(message));
        view.onNextActionsUpdate(new NextActionsUpdateMessage(nextActions.getFirst(), nextActions.getSecond()));
    }
}
