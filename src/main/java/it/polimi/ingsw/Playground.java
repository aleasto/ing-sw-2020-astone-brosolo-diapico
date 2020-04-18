package it.polimi.ingsw;

import it.polimi.ingsw.Game.Game;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Worker;
import it.polimi.ingsw.View.CLIView;
import it.polimi.ingsw.View.View;

import java.util.ArrayList;
import java.util.List;

public class Playground {
    public static void main(String args[]) {
        Player me = new Player("bruh", "Artemis", 3);
        List<Player> players = new ArrayList<Player>();
        players.add(me);
        Game game = new Game(players);
        CLIView view = new CLIView(me);
        view.registerObserver(game);
        game.getBoard().registerObserver(view.getBoardObserver());
        game.getStorage().registerObserver(view.getStorageObserver());
        game.registerObserver(view.getErrorObserver());

        Thread cliThread = new Thread(view);
        cliThread.start();

        Worker w = new Worker(me, game.getBoard().getAt(2,2));
    }
}
