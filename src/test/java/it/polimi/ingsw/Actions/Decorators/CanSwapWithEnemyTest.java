package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Actions.Decorators.CanSwapWithEnemy;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CanSwapWithEnemyTest {

    @Test
    void validMove() {
        Actions myActions = new BaseActions();
        myActions = new CanSwapWithEnemy(myActions);

        Tile sourceTile = new Tile(2, 2);
        Tile destinationTile = new Tile(2, 3);
        Player me = new Player();
        Player enemy = new Player();
        Worker myWorker = new Worker(me, sourceTile);
        Worker enemyWorker = new Worker(enemy, destinationTile);

        // I can swap position with an enemy worker
        assertTrue(myActions.validMove(myWorker, destinationTile));

        // But I can't with an allied worker
        Worker myOtherWorker = new Worker(me, destinationTile);
        assertFalse(myActions.validMove(myWorker, destinationTile));
    }

    @Test
    void postMove() {
        Actions myActions = new BaseActions();
        myActions = new CanSwapWithEnemy(myActions);

        Tile sourceTile = new Tile(2, 2);
        Tile destinationTile = new Tile(2, 3);
        Worker myWorker = new Worker(new Player(), sourceTile);
        Worker enemyWorker = new Worker(new Player(), destinationTile);

        // Mimic the workflow of a move
        assertTrue(myActions.validMove(myWorker, destinationTile));
        myWorker.setTile(destinationTile);
        destinationTile.setOccupant(myWorker);
        myActions.postMove(myWorker, sourceTile);

        // Make sure I end up in destinationTile, and enemy in sourceTile
        assertEquals(sourceTile, enemyWorker.getTile());
        assertEquals(enemyWorker, sourceTile.getOccupant());
        assertEquals(destinationTile, myWorker.getTile());
        assertEquals(myWorker, destinationTile.getOccupant());
    }
}
