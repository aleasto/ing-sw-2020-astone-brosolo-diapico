package it.polimi.ingsw.Server;

import it.polimi.ingsw.Game.Game;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.View.Comunication.BuildCommandMessage;
import it.polimi.ingsw.View.Comunication.EndTurnCommandMessage;
import it.polimi.ingsw.View.Comunication.MoveCommandMessage;
import it.polimi.ingsw.View.ServerRemoteView;
import it.polimi.ingsw.View.View;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Lobby {
    List<Player> players = new ArrayList<Player>();
    List<ServerRemoteView> remoteViews = new ArrayList<ServerRemoteView>();

    public void connect(Socket client, Player player) {
        players.add(player);
        remoteViews.add(new ServerRemoteView(client, player));

        System.out.println("Client entered lobby");

        // TODO: Change this...
        if (players.size() == 1) {
            startGame();
        }
    }

    public void startGame() {
        System.out.println("Game started!");
        Game game = new Game(players);
        for (ServerRemoteView view : remoteViews) {
            new Thread(view).start();

            view.addMoveCommandListener((MoveCommandMessage message) ->
                    game.Move(view.getPlayer(), message.getFromX(), message.getFromY(), message.getToX(), message.getToY()));
            view.addBuildCommandListener((BuildCommandMessage message) ->
                    game.Build(view.getPlayer(), message.getFromX(), message.getFromY(), message.getToX(), message.getToY(), message.getBlock()));
            view.addEndTurnCommandListener((EndTurnCommandMessage message) -> game.EndTurn(view.getPlayer()));

            game.getBoard().addBoardUpdateListener(view);
            game.getStorage().addStorageUpdateListener(view);
            game.addNextActionsUpdateListener(view);
            game.addTextListener(view);
        }
    }
}
