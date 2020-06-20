package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanExtraBuildALevelTest {

    //Tests that you can build two levels on the same tile and not on any other tile
    //Also tests that you can't build after the two scheduled builds
    @Test
    void validBuild() {
        Actions myAction = new BaseActions();
        myAction = new CanBuildTwice(myAction);
        myAction = new CanExtraBuildALevel(myAction);

        Board board = new Board(5, 5, 3);
        Tile startingTile = board.getAt(3, 3);
        Tile correctBuild = board.getAt(2, 2);
        Tile incorrectBuild = board.getAt(2, 3);
        Worker w = new Worker(new Player("", 0), startingTile);

        //Performs the first build
        assertTrue(myAction.validBuild(w, correctBuild, correctBuild.getHeight()));
        myAction.doBuild(w, correctBuild, correctBuild.getHeight());

        //Checks that you can only build on the same tile as the last one
        assertFalse(myAction.validBuild(w, incorrectBuild, incorrectBuild.getHeight()));
        assertTrue(myAction.validBuild(w, correctBuild, correctBuild.getHeight()));
        //And not a dome
        for (int i = 0; i < board.getMaxHeight() - 1; i++) {
            correctBuild.buildUp();
        }
        assertEquals(correctBuild.getHeight(), board.getMaxHeight());
        assertFalse(correctBuild.hasDome());
        assertFalse(myAction.validBuild(w, correctBuild, correctBuild.getHeight()));
        //For testing purposes we build anyway, but usually we wouldn't be able to build here
        myAction.doBuild(w, correctBuild, correctBuild.getHeight());

        //Checks that you can't build afterwards
        assertFalse(myAction.canBuild());
    }
}
