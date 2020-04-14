package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Actions.Decorators.CanBuildTwice;
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

        Tile startingTile = new Tile(3, 3);
        Tile firstBuild = new Tile(2, 2);
        Tile secondBuild = new Tile(2, 3);
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

        Tile startingTile = new Tile(3, 3);
        Tile firstBuild = new Tile(2, 2);
        Tile secondBuild = new Tile(2, 3);
        Tile notValidBuild = new Tile(5, 5);
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
