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
        Worker w = new Worker(new Player(), destinationTile);
        myActions.postMove(w, sourceTile);
        assertTrue(myActions.canMove());

        // Now we move in reverse, from destinationTile to sourceTile
        sourceTile.setOccupant(w);
        w.setTile(sourceTile);
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