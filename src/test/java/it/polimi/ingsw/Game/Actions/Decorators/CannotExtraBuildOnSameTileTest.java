package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CannotExtraBuildOnSameTileTest {

    //Tests that you can't build before moving and that you can build only on valid tiles
    @Test
    void validBuild() {
        Actions myAction = new BaseActions();
        myAction = new CanBuildTwice(myAction);
        myAction = new CannotExtraBuildOnSameTile(myAction);

        Board board = new Board(5, 5, 3);
        Tile startingTile = board.getAt(3, 3);
        Tile firstBuild = board.getAt(2, 2);
        Tile secondBuild = board.getAt(2, 3);
        Tile notValidBuild = board.getAt(0, 0);
        Worker w = new Worker(new Player("", 0), startingTile);

        //Builds the first time
        assertTrue(myAction.validBuild(w, firstBuild, firstBuild.getHeight()));
        myAction.doBuild(w, firstBuild, firstBuild.getHeight());
        assertTrue(myAction.canBuild());

        //Verifies that you can build the second time only in a valid and different tile from the previous one
        assertFalse(myAction.validBuild(w, firstBuild, firstBuild.getHeight()));
        assertFalse(myAction.validBuild(w, notValidBuild, notValidBuild.getHeight()));
        assertTrue(myAction.validBuild(w, secondBuild, secondBuild.getHeight()));
        myAction.doBuild(w, secondBuild, secondBuild.getHeight());
        assertFalse(myAction.canBuild());
    }
}
