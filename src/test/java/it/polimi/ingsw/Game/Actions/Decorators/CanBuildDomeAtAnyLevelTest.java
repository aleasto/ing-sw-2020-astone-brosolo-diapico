package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanBuildDomeAtAnyLevelTest {

    @Test
    void canBuildDome() {
        Actions myAction = new BaseActions();
        myAction = new CanBuildDomeAtAnyLevel(myAction);

        Board board = new Board();
        Tile myTile = board.getAt(2, 2);
        Tile buildTile = board.getAt(3, 3);
        Tile badTile = board.getAt(2, 3);
        Worker myW = new Worker(new Player(), myTile);

        assertTrue(myAction.validBuild(myW, buildTile, Tile.getMaxHeight()));
        assertFalse(myAction.validBuild(myW, badTile, 2)); // can't build just any block
        badTile.buildDome();
        assertFalse(myAction.validBuild(myW, badTile, Tile.getMaxHeight())); // dome is already placed

        myAction.doBuild(myW, buildTile, 3);
        assertTrue(buildTile.hasDome());
        assertEquals(0, buildTile.getHeight());
    }
}
