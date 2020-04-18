package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Actions.Decorators.CanHeadbutt;
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
        Actions enemyActions = new BaseActions();
        Board board = new Board();
        Tile sourceTile = board.getAt(3,3);
        Tile wrongTile = board.getAt(1,1);
        Tile rightTile = board.getAt(2,2);
        Tile secondaryTile = board.getAt(3,2);

        Player me = new Player();
        Player enemy = new Player();
        Worker myWorker = new Worker(me, sourceTile);
        Worker enemyWorker = new Worker(enemy, rightTile);
        Worker enemyWorker2 = new Worker(enemy, wrongTile);
        Worker myWorker2 = new Worker(me, secondaryTile);

        assertFalse(myActions.validMove(myWorker, rightTile));
        assertFalse(myActions.validMove(myWorker, secondaryTile));
    }
}
