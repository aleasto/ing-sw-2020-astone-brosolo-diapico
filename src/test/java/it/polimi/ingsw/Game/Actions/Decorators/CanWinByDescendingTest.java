package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanWinByDescendingTest {

    @Test
    void winWhenDescending() {
        Actions myAction = new BaseActions();
        myAction = new CanWinByDescending(myAction);

        Board board = new Board(5, 5, 3);
        Tile startingTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(3, 3);
        Tile badDestTile = board.getAt(3, 2);
        startingTile.buildUp();
        startingTile.buildUp();
        badDestTile.buildUp();
        badDestTile.buildUp();
        Worker w = new Worker(new Player("", 0), startingTile);

        assertTrue(myAction.doMove(w, destinationTile));
        destinationTile.buildUp();
        destinationTile.buildUp();
        assertFalse(myAction.doMove(w, badDestTile));
    }
}
