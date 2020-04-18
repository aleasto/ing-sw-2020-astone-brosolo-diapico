package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CannotMoveUpIfEnemyDidTest {

    @Test
    void validMove() {
        // e.g. enemy is athena
        Actions enemyActions = new BaseActions();
        Actions myActions = new BaseActions();
        myActions = new CannotMoveUpIfEnemyDid(myActions, enemyActions /* the enemy that controls this effect */);

        Board board = new Board();
        Tile mySrc = board.getAt(2, 2);
        Tile myDst = board.getAt(2, 3);
        myDst.buildUp();
        Worker myWorker = new Worker(new Player(), mySrc);

        Tile enemySrc = board.getAt(1, 1);
        Tile enemyDst = board.getAt(1, 2);
        enemyDst.buildUp();
        Worker enemyWorker = new Worker(new Player(), enemySrc);

        // Normally I can move up
        assertTrue(myActions.validMove(myWorker, myDst));

        // But if enemy moves up...
        enemyActions.doMove(enemyWorker, enemyDst);

        // I can't move up anymore
        assertFalse(myActions.validMove(myWorker, myDst));
    }
}
