package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AllWorkersCanMoveIndefinitelyOnPlaneTest {

    @Test
    void canMove() {
        Actions myActions = new BaseActions();
        myActions = new AllWorkersCanMoveIndefinitelyOnPlane(myActions);
        Board board = new Board(5, 5, 3);
        Player p = new Player("", 0);
        Worker w = new Worker(p, board.getAt(0, 0));

        for (int i = 1; i < 4; i++) {
            assertTrue(myActions.canMove());
            myActions.doMove(w, board.getAt(0, i));
        }

        myActions.doBuild(w, board.getAt(1, 4), 0);
        assertFalse(myActions.canMove());

        myActions.beginTurn();
        assertTrue(myActions.canMove());
        myActions.doMove(w, board.getAt(1, 4)); // move up
        assertFalse(myActions.canMove());
    }

    @Test
    void canBuild() {
        Actions myActions = new BaseActions();
        myActions = new AllWorkersCanMoveIndefinitelyOnPlane(myActions);
        Board board = new Board(5, 5, 3);
        Player p = new Player("", 0);
        Worker w = new Worker(p, board.getAt(0, 0));

        assertTrue(myActions.canBuild());
        myActions.doMove(w, board.getAt(0, 1));
        assertTrue(myActions.canBuild());
        myActions.doBuild(w, board.getAt(0, 0), 0);
        assertFalse(myActions.canBuild());
    }

    @Test
    void canUseThisWorkerNow() {
        Actions myActions = new BaseActions();
        myActions = new AllWorkersCanMoveIndefinitelyOnPlane(myActions);
        Board board = new Board(5, 5, 3);
        Player p = new Player("", 0);
        Worker w1 = new Worker(p, board.getAt(0, 0));
        Worker w2 = new Worker(p, board.getAt(4, 4));

        assertTrue(myActions.canUseThisWorkerNow(w1));
        assertTrue(myActions.canUseThisWorkerNow(w2));
        myActions.doMove(w1, board.getAt(0, 1));
        assertTrue(myActions.canUseThisWorkerNow(w1));
        assertTrue(myActions.canUseThisWorkerNow(w2));
        myActions.doMove(w2, board.getAt(4, 3));
        assertTrue(myActions.canUseThisWorkerNow(w1));
        assertTrue(myActions.canUseThisWorkerNow(w2));

        myActions.doBuild(w1, board.getAt(0, 0), 0);
        myActions.beginTurn();

        myActions.doMove(w1, board.getAt(0, 0)); // move up
        assertTrue(myActions.canUseThisWorkerNow(w1));
        assertFalse(myActions.canUseThisWorkerNow(w2));
    }

    @Test
    void validMove() {
        Actions myActions = new BaseActions();
        myActions = new AllWorkersCanMoveIndefinitelyOnPlane(myActions);
        Board board = new Board(5, 5, 3);
        Player p = new Player("", 0);
        Worker w = new Worker(p, board.getAt(0, 0));

        for (int i = 1; i < 4; i++) {
            board.getAt(0, i).buildUp();
        }
        myActions.doMove(w, board.getAt(0, 1));
        myActions.beginTurn();

        // We can both move down and on the same level
        assertTrue(myActions.validMove(w, board.getAt(0, 0)));
        assertTrue(myActions.validMove(w, board.getAt(0, 2)));

        // Once I move on the same level, I can move again but on the same level only
        myActions.doMove(w, board.getAt(0, 2));
        assertTrue(myActions.canMove());
        assertTrue(myActions.validMove(w, board.getAt(0, 3)));
        assertFalse(myActions.validMove(w, board.getAt(1, 2)));
    }
}