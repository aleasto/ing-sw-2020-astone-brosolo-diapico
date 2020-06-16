package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CannotWinOnPerimeterTest {

    @Test
    void doNotWinOnPerimeter() {
        Actions enemyActions = new BaseActions();
        Actions myActions = new BaseActions();
        myActions = new CannotWinOnPerimeter(myActions, enemyActions /* the enemy that controls this effect */);

        Board board = new Board(5, 5, 3);
        Tile mySrc = board.getAt(2, 0);
        Tile myDst = board.getAt(3, 0);
        Tile ndSrc = board.getAt(2, 4);
        Tile ndDst = board.getAt(3, 4);

        //Make it so our next move will make us win on the perimeter
        mySrc.buildUp();
        mySrc.buildUp();

        myDst.buildUp();
        myDst.buildUp();
        myDst.buildUp();

        //Same for the second set of tiles
        ndSrc.buildUp();
        ndSrc.buildUp();

        ndDst.buildUp();
        ndDst.buildUp();
        ndDst.buildUp();

        Worker myWorker = new Worker(new Player("", 0), mySrc);
        Worker ndWorker = new Worker(new Player("", 0), ndSrc);

        //Assert that I can't win on the perimeter but I actually am in the highest possible height
        assertFalse(myActions.doMove(myWorker, myDst));
        assertTrue(myWorker.getTile().getHeight() == board.getMaxHeight());

        // If enemy loses I can win on the perimeter
        enemyActions.onLose();
        assertTrue(myActions.doMove(ndWorker, ndDst));
    }
}
