package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CanInteractWithEnemyTest {

    @Test
    void validMove() {
        Actions myActions = new BaseActions();
        myActions = new CanInteractWithEnemy(myActions);

        Board board = new Board(5, 5, 3);
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        Player me = new Player("", 0);
        Player enemy = new Player("", 0);
        Worker myWorker = new Worker(me, sourceTile);
        Worker enemyWorker = new Worker(enemy, destinationTile);

        // I can swap position with an enemy worker
        assertTrue(myActions.validMove(myWorker, destinationTile));

        // But I can't with an allied worker
        Worker myOtherWorker = new Worker(me, destinationTile);
        assertFalse(myActions.validMove(myWorker, destinationTile));
    }
}
