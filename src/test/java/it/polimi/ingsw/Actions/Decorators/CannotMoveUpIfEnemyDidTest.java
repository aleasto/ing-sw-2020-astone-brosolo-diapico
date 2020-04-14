package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Actions.Decorators.CannotMoveUpIfEnemyDid;
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

        Tile mySrc = new Tile(2, 2);
        Tile myDst = new Tile(2, 3);
        myDst.buildUp();
        Worker myWorker = new Worker(new Player(), mySrc);

        Tile enemySrc = new Tile(1, 1);
        Tile enemyDst = new Tile(1, 2);
        enemyDst.buildUp();
        Worker enemyWorker = new Worker(new Player(), enemySrc);

        // Normally I can move up
        assertTrue(myActions.validMove(myWorker, myDst));

        // But if enemy moves up...
        enemyWorker.setTile(enemyDst);
        enemyDst.setOccupant(enemyWorker);
        enemyActions.postMove(enemyWorker, enemySrc);

        // I can't move up anymore
        assertFalse(myActions.validMove(myWorker, myDst));
    }
}
