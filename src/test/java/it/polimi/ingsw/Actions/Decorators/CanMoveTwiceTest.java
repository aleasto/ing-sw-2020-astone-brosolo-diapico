package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Actions.Decorators.CanMoveTwice;
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

        Tile sourceTile = new Tile(2, 2);
        Tile destinationTile = new Tile(2, 3);
        Worker w = new Worker(new Player(), sourceTile);
        myActions.doMove(w, destinationTile);
        assertTrue(myActions.canMove());

        // Now we move in reverse, from destinationTile to sourceTile
        myActions.doMove(w, sourceTile);
        assertFalse(myActions.canMove());
    }

    @Test
    void validMove() {
        Actions myActions = new BaseActions();
        myActions = new CanMoveTwice(myActions);
        assertTrue(myActions.canMove());

        Tile sourceTile = new Tile(2, 2);
        Tile destinationTile = new Tile(2, 3);
        Worker w = new Worker(new Player(), sourceTile);
        assertTrue(myActions.validMove(w, destinationTile));
        myActions.doMove(w, destinationTile);

        assertTrue(myActions.validMove(w, new Tile(3, 3)));
        assertFalse(myActions.validMove(w, sourceTile));
    }
}