package it.polimi.ingsw.Game.Actions;

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

        Board board = new Board(5, 5, 3);
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        Worker w = new Worker(new Player("", 0), sourceTile);
        myActions.doMove(w, destinationTile);
        assertFalse(myActions.canMove());
    }

    @Test
    void validMove() {
        BaseActions myActions = new BaseActions();

        Board board = new Board(5, 5, 3);
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        Worker w = new Worker(new Player("", 0), sourceTile);
        assertTrue(myActions.validMove(w, destinationTile));

        destinationTile.buildUp();
        assertTrue(myActions.validMove(w, destinationTile));

        destinationTile.buildUp();
        assertFalse(myActions.validMove(w, destinationTile));

        destinationTile = board.getAt(0, 4);
        assertFalse(myActions.validMove(w, destinationTile));

        // Can't move into an occupied tile
        destinationTile = board.getAt(2, 3);
        Worker enemyWorker = new Worker(new Player("", 0), destinationTile);
        assertFalse(myActions.validMove(w, destinationTile));
    }

    @Test
    void doMove() {
        BaseActions myActions = new BaseActions();

        Board board = new Board(5, 5, 3);
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        // Build up 2 times
        for (int i = 0; i < 2; i++) {
            sourceTile.buildUp();
        }
        for (int i = 0; i < 2; i++) {
            destinationTile.buildUp();
        }
        Worker w = new Worker(new Player("", 0), sourceTile);

        // I only win if i move on top of a 3-storey building
        assertFalse(myActions.doMove(w, destinationTile));
        assertFalse(myActions.doMove(w, sourceTile));
        destinationTile.buildUp();
        assertTrue(myActions.doMove(w, destinationTile));
        // But i don't win if i don't move up
        sourceTile.buildUp();
        assertFalse(myActions.doMove(w, sourceTile));
    }

    @Test
    void canBuild() {
        BaseActions myActions = new BaseActions();

        assertFalse(myActions.canBuild());

        Board board = new Board(5, 5, 3);
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        Worker w = new Worker(new Player("", 0), sourceTile);
        myActions.doMove(w, destinationTile);
        assertTrue(myActions.canBuild());

        myActions.doBuild(w, destinationTile, destinationTile.getHeight());
        assertFalse(myActions.canBuild());
    }

    @Test
    void validBuild() {
        BaseActions myActions = new BaseActions();
        Board board = new Board(5, 5, 3);
        Tile sourceTile = board.getAt(2, 2);
        Worker w = new Worker(new Player("", 0), sourceTile);

        Tile destinationTile = board.getAt(2, 3);
        assertTrue(myActions.validBuild(w, destinationTile, destinationTile.getHeight()));
        assertFalse(myActions.validBuild(w, destinationTile, destinationTile.getHeight() + 1));
        destinationTile = board.getAt(0, 4);
        assertFalse(myActions.validBuild(w, destinationTile, destinationTile.getHeight()));
    }

    @Test
    void getLastMove() {
        BaseActions myActions = new BaseActions();
        Board board = new Board(5, 5, 3);
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        Worker w = new Worker(new Player("", 0), sourceTile);

        myActions.doMove(w, destinationTile);
        Pair<Tile, Tile> lastMove = myActions.getLastMove();
        assertEquals(sourceTile, lastMove.getFirst());
        assertEquals(destinationTile, lastMove.getSecond());
    }

    @Test
    void cantBuildNorMoveOnADome() {
        BaseActions myActions = new BaseActions();
        Board board = new Board(5, 5, 3);
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);

        destinationTile.buildDome();

        Worker myWorker = new Worker(new Player("", 0), sourceTile);
        assertFalse(myActions.validBuild(myWorker, destinationTile,0));
        assertFalse(myActions.validMove(myWorker, destinationTile));
    }

    @Test
    void mustMove() {
        Actions myActions = new BaseActions();
        Board board = new Board(5, 5, 3);
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        Worker w = new Worker(new Player("", 0), sourceTile);

        assertTrue(myActions.mustMove());
        myActions.doMove(w, destinationTile);
        assertFalse(myActions.mustMove());
    }

    @Test
    void mustBuild() {
        Actions myActions = new BaseActions();
        Board board = new Board(5, 5, 3);
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        Worker w = new Worker(new Player("", 0), sourceTile);

        assertTrue(myActions.mustBuild());
        myActions.doMove(w, destinationTile);
        assertTrue(myActions.mustBuild());
        myActions.doBuild(w, sourceTile, sourceTile.getHeight());
        assertFalse(myActions.mustBuild());
    }

    @Test
    void canUseThisWorkerNow() {
        Actions myActions = new BaseActions();
        Board board = new Board(5, 5, 3);
        Tile sourceTile1 = board.getAt(2, 2);
        Tile sourceTile2 = board.getAt(1, 1);
        Tile destinationTile = board.getAt(2, 3);
        Player p = new Player("", 0);
        Worker w1 = new Worker(p, sourceTile1);
        Worker w2 = new Worker(p, sourceTile2);

        // I can use any worker at the start of a turn
        assertTrue(myActions.canUseThisWorkerNow(w1));
        assertTrue(myActions.canUseThisWorkerNow(w2));

        // Let's do a move with worker1
        assertTrue(myActions.validMove(w1, destinationTile));
        myActions.doMove(w1, destinationTile);

        // Theoretically worker2 could have a valid build action
        assertTrue(myActions.validBuild(w2, sourceTile1, 0));
        // But I cannot use a different worker in the same turn!
        assertFalse(myActions.canUseThisWorkerNow(w2));
    }
}
