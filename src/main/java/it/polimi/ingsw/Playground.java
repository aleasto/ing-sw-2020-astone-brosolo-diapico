package it.polimi.ingsw;

import it.polimi.ingsw.Exceptions.InvalidBuildActionException;
import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Exceptions.InvalidMoveActionException;
import it.polimi.ingsw.Game.Game;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Worker;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.View.CLIView;
import it.polimi.ingsw.View.Comunication.*;
import it.polimi.ingsw.View.Comunication.Listeners.BuildCommandListener;
import it.polimi.ingsw.View.Comunication.Listeners.MoveCommandListener;
import it.polimi.ingsw.View.View;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Playground {
    static Game game;

    public static void main(String args[]) {
        Player me = new Player("bruh", "Artemis", 3);
        List<Player> players = new ArrayList<Player>();
        players.add(me);
        game = new Game(players);
        Worker w = new Worker(me, game.getBoard().getAt(2,2));

        CLIView view = new CLIView(me);
        Thread cliThread = new Thread(view);
        cliThread.start();

        // We listen for commands coming from the view, and invoke methods on the model, of which `game` is the entry point
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
                game.EndTurn(view.getPlayer());
                promptNextAction(view, "It's your turn. What do you do?");
            } catch (InvalidCommandException e) {
                view.onText(new TextMessage(e.getMessage()));
            }
        });
        promptNextAction(view, "Welcome!");

        // Also the view listens for model updates
        game.getBoard().addBoardUpdateListener(view);
        game.getStorage().addStorageUpdateListener(view);
    }

    static void promptNextAction(View view, String message) {
        Pair<List<MoveCommandMessage>, List<BuildCommandMessage>> nextActions = game.computeAvailableActions(view.getPlayer());
        view.onText(new TextMessage(message));
        view.onNextActionsUpdate(new NextActionsUpdateMessage(nextActions.getFirst(), nextActions.getSecond()));
    }
}
