package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CanMoveTwiceTest {

    @Test
    void canMove() {
        Actions myActions = new BaseActions();
        myActions = new CanMoveTwice(myActions);
        assertTrue(myActions.canMove());

        Board board = new Board(5, 5, 3);
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        Worker w = new Worker(new Player("", 0), sourceTile);
        myActions.doMove(w, destinationTile);
        assertTrue(myActions.canMove());

        // Now we move in reverse, from destinationTile to sourceTile
        myActions.doMove(w, sourceTile);
        assertFalse(myActions.canMove());
    }
}