package it.polimi.ingsw.Actions;

import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseActionsTest {

    @Test
    void canMove() {
        BaseActions myActions = new BaseActions();
        assertTrue(myActions.canMove());

        Board board = new Board();
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        Worker w = new Worker(new Player(), sourceTile);
        myActions.doMove(w, destinationTile);
        assertFalse(myActions.canMove());
    }

    @Test
    void validMove() {
        BaseActions myActions = new BaseActions();

        Board board = new Board();
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        Worker w = new Worker(new Player(), sourceTile);
        assertTrue(myActions.validMove(w, destinationTile));

        destinationTile.buildUp();
        assertTrue(myActions.validMove(w, destinationTile));

        destinationTile.buildUp();
        assertFalse(myActions.validMove(w, destinationTile));

        destinationTile = board.getAt(0, 4);
        assertFalse(myActions.validMove(w, destinationTile));

        // Can't move into an occupied tile
        destinationTile = board.getAt(2, 3);
        Worker enemyWorker = new Worker(new Player(), destinationTile);
        assertFalse(myActions.validMove(w, destinationTile));
    }

    @Test
    void postMove() {
        BaseActions myActions = new BaseActions();

        Board board = new Board();
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        // Build up 2 times
        for (int i = 0; i < 2; i++) {
            sourceTile.buildUp();
        }
        for (int i = 0; i < 2; i++) {
            destinationTile.buildUp();
        }
        Worker w = new Worker(new Player(), sourceTile);

        // I only win if i move on top of a 3-storey building
        assertFalse(myActions.doMove(w, destinationTile));
        assertFalse(myActions.doMove(w, sourceTile));
        destinationTile.buildUp();
        assertTrue(myActions.doMove(w, destinationTile));
    }

    @Test
    void canBuild() {
        BaseActions myActions = new BaseActions();

        assertFalse(myActions.canBuild());

        Board board = new Board();
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        Worker w = new Worker(new Player(), sourceTile);
        myActions.doMove(w, destinationTile);
        assertTrue(myActions.canBuild());

        myActions.doBuild(w, destinationTile, destinationTile.getHeight());
        assertFalse(myActions.canBuild());
    }

    @Test
    void validBuild() {
        BaseActions myActions = new BaseActions();
        Board board = new Board();
        Tile sourceTile = board.getAt(2, 2);
        Worker w = new Worker(new Player(), sourceTile);

        Tile destinationTile = board.getAt(2, 3);
        assertTrue(myActions.validBuild(w, destinationTile, destinationTile.getHeight()));
        assertFalse(myActions.validBuild(w, destinationTile, destinationTile.getHeight() + 1));
        destinationTile = board.getAt(0, 4);
        assertFalse(myActions.validBuild(w, destinationTile, destinationTile.getHeight()));
    }

    @Test
    void getLastMove() {
        BaseActions myActions = new BaseActions();
        Board board = new Board();
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        Worker w = new Worker(new Player(), sourceTile);

        myActions.doMove(w, destinationTile);
        Pair<Tile, Tile> lastMove = myActions.getLastMove();
        assertEquals(sourceTile, lastMove.getFirst());
        assertEquals(destinationTile, lastMove.getSecond());
    }
}
