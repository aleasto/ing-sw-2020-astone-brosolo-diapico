package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Actions.Decorators.CanBuildTwice;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanBuildTwiceTest {

    //Test that you can't build thrice
    @Test
    void canBuild() {
        Actions myAction = new BaseActions();
        myAction = new CanBuildTwice(myAction);

        Board board = new Board();
        Tile startingTile = board.getAt(3, 3);
        Tile firstBuild = board.getAt(2, 2);
        Tile secondBuild = board.getAt(2, 3);
        Worker w = new Worker(new Player(), startingTile);

        myAction.doBuild(w, firstBuild, firstBuild.getHeight());
        assertTrue(myAction.canBuild());

        myAction.doBuild(w, secondBuild, secondBuild.getHeight());
        assertFalse(myAction.canBuild());
    }

    //Tests that you can't build before moving and that you can build only on valid tiles
    @Test
    void validBuild() {
        Actions myAction = new BaseActions();
        myAction = new CanBuildTwice(myAction);

        Board board = new Board();
        Tile startingTile = board.getAt(3, 3);
        Tile firstBuild = board.getAt(2, 2);
        Tile secondBuild = board.getAt(2, 3);
        Tile notValidBuild = board.getAt(0, 0);
        Worker w = new Worker(new Player(), startingTile);


        //Builds the first time
        assertTrue(myAction.validBuild(w, firstBuild, firstBuild.getHeight()));
        myAction.doBuild(w, firstBuild, firstBuild.getHeight());

        //Verifies that you can build the second time only in a valid and different tile from the previous one
        assertFalse(myAction.validBuild(w, firstBuild, firstBuild.getHeight()));
        assertFalse(myAction.validBuild(w, notValidBuild, notValidBuild.getHeight()));
        assertTrue(myAction.validBuild(w, secondBuild, secondBuild.getHeight()));
    }
}
