package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CanSwapWithEnemyTest {

    @Test
    void Move() {
        Actions myActions = new BaseActions();
        myActions = new CanSwapWithEnemy(myActions);

        Board board = new Board(5, 5, 3);
        Tile sourceTile = board.getAt(2, 2);
        Tile destinationTile = board.getAt(2, 3);
        Worker myWorker = new Worker(new Player("", 0), sourceTile);
        Worker enemyWorker = new Worker(new Player("", 0), destinationTile);

        // Make the move
        myActions.doMove(myWorker, destinationTile);

        // Make sure I end up in destinationTile, and enemy in sourceTile
        assertEquals(sourceTile, enemyWorker.getTile());
        assertEquals(enemyWorker, sourceTile.getOccupant());
        assertEquals(destinationTile, myWorker.getTile());
        assertEquals(myWorker, destinationTile.getOccupant());
    }
}
