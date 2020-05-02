package it.polimi.ingsw;

import it.polimi.ingsw.Game.Game;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Worker;
import it.polimi.ingsw.View.CLIView;
import it.polimi.ingsw.View.Comunication.BuildCommandMessage;
import it.polimi.ingsw.View.Comunication.EndTurnCommandMessage;
import it.polimi.ingsw.View.Comunication.Listeners.BuildCommandListener;
import it.polimi.ingsw.View.Comunication.Listeners.MoveCommandListener;
import it.polimi.ingsw.View.Comunication.MoveCommandMessage;
import it.polimi.ingsw.View.View;

import java.util.ArrayList;
import java.util.List;

public class Playground {
    public static void main(String args[]) {
        Player me = new Player("bruh", "Artemis", 3);
        List<Player> players = new ArrayList<Player>();
        players.add(me);
        Game game = new Game(players);
        Worker w = new Worker(me, game.getBoard().getAt(2,2));

        CLIView view = new CLIView(me);
        Thread cliThread = new Thread(view);
        cliThread.start();

        // We listen for commands coming from the view, and invoke methods on the model, of which `game` is the entry point
        view.addMoveCommandListener((MoveCommandMessage message) ->
                game.Move(me, message.getFromX(), message.getFromY(), message.getToX(), message.getToY()));
        view.addBuildCommandListener((BuildCommandMessage message) ->
                game.Build(me, message.getFromX(), message.getFromY(), message.getToX(), message.getToY(), message.getBlock()));
        view.addEndTurnCommandListener((EndTurnCommandMessage message) -> game.EndTurn(me));

        // Also the view listens for model updates
        game.getBoard().addBoardUpdateListener(view);
        game.getStorage().addStorageUpdateListener(view);
        game.addNextActionsUpdateListener(view);
        game.addTextListener(view);
    }
}
