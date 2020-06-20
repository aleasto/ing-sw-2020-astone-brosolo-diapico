package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoveInfinitelyOnPerimeterTest {

    @Test
    void moveInfinitely() {
        Actions myAction = new BaseActions();
        myAction = new MoveInfinitelyOnPerimeter(myAction);

        Board board = new Board(5, 5, 3);
        Tile startingTile = board.getAt(1, 1);
        Tile secondTile = board.getAt(0, 0);
        Tile thirdTile = board.getAt(0, 1);
        Tile fourthTile = board.getAt(1, 2);
        Worker w = new Worker(new Player("", 0), startingTile);

        myAction.doMove(w, secondTile);
        assertTrue(myAction.canMove());

        myAction.doMove(w, thirdTile);
        assertTrue(myAction.canMove());

        myAction.doMove(w, fourthTile);
        assertFalse(myAction.canMove());
    }
}
