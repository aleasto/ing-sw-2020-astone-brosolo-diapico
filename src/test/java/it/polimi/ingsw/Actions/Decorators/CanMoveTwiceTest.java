package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Actions.Actions;
import it.polimi.ingsw.Actions.BaseActions;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.Tile;
import it.polimi.ingsw.Worker;
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
        Worker w = new Worker(new Player(), destinationTile);
        myActions.postMove(w, sourceTile);
        assertTrue(myActions.canMove());

        myActions.postMove(w, destinationTile);
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

        w.setTile(destinationTile);
        destinationTile.setOccupant(w);
        myActions.postMove(w, sourceTile);

        assertTrue(myActions.validMove(w, new Tile(3, 3)));
        assertFalse(myActions.validMove(w, sourceTile));
    }
}