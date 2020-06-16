package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanHeadbuttTest {

    @Test
    void validMove() {
        Actions myActions = new BaseActions();
        myActions = new CanHeadbutt(myActions);
        Board board = new Board(5, 5, 3);

        //We retrieve interesting tiles
        Tile sourceTile = board.getAt(3,3);
        Tile pushedTile = board.getAt(1,1);
        Tile rightTile = board.getAt(2,2);
        Tile secondaryTile = board.getAt(3,2);
        Tile boundsTile = board.getAt(4,3);

        //We populate the tiles
        Player me = new Player("", 0);
        Player enemy = new Player("", 0);
        Worker myWorker = new Worker(me, sourceTile);
        Worker enemyWorker = new Worker(enemy, rightTile);
        assertTrue(myActions.validMove(myWorker, rightTile));

        //We verify that I can't push onto an occupied tile
        Worker enemyWorker2 = new Worker(enemy, pushedTile);
        assertFalse(myActions.validMove(myWorker, rightTile));
        rightTile.setOccupant(null);
        rightTile.buildDome();
        assertFalse(myActions.validMove(myWorker, rightTile));

        //And that I can't push my own worker
        Worker myWorker2 = new Worker(me, secondaryTile);
        assertFalse(myActions.validMove(myWorker, secondaryTile));

        //And that I can't push outside of the board
        Worker enemyWorker3 = new Worker(enemy, boundsTile);
        assertFalse(myActions.validMove(myWorker, boundsTile));
    }

    @Test
    void doMove() {
        Actions myActions = new BaseActions();
        myActions = new CanHeadbutt(myActions);

        Board board = new Board(5, 5, 3);
        Tile sourceTile = board.getAt(2,2);
        Tile destTile = board.getAt(3,3);
        Tile pushedTile = board.getAt(4,4);
        Worker myWorker = new Worker(new Player("", 0), sourceTile);
        Worker enemyWorker = new Worker(new Player("", 0), destTile);

        //We first verify if the move is valid, this helps us building the internal stored tile, then we actually move.
        myActions.validMove(myWorker, destTile);
        myActions.doMove(myWorker, destTile);

        //We verify whether the enemy was pushed onto the right tile and that we effectively moved.
        assertEquals(enemyWorker, pushedTile.getOccupant());
        assertEquals(pushedTile, enemyWorker.getTile());
        assertEquals(destTile, myWorker.getTile());
        assertEquals(myWorker, destTile.getOccupant());
    }
}
