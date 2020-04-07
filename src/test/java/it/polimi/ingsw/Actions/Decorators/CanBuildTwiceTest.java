package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Actions.Actions;
import it.polimi.ingsw.Actions.BaseActions;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.Tile;
import it.polimi.ingsw.Worker;
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

        myAction.postBuild(w, firstBuild);
        assertTrue(myAction.canBuild());

        myAction.postBuild(w, secondBuild);
        assertFalse(myAction.canBuild());
    }

    //Tests that you can't build before moving and that you can build only on valid tiles
    @Test
    void validBuild() {
        Actions myAction = new BaseActions();
        myAction = new CanBuildTwice(myAction);

        Tile startingTile = new Tile(2, 2);
        Tile movingTile = new Tile(3, 3);
        Tile firstBuild = new Tile(2, 2);
        Tile secondBuild = new Tile(2, 3);
        Tile notValidBuild = new Tile(5, 5);
        Worker w = new Worker(new Player(), startingTile);

        //Verifies you can't build before moving
        assertFalse(myAction.validBuild(w, firstBuild));

        //Fakes the movement and verifies that now I can build
        w.setTile(movingTile);
        myAction.postMove(w, startingTile);
        assertTrue(myAction.canBuild());

        //Builds the first time
        assertTrue(myAction.validBuild(w, firstBuild));
        myAction.postBuild(w,firstBuild);

        //Verifies that you can build the second time only in a valid and different tile from the previous one
        assertFalse(myAction.validBuild(w, firstBuild));
        assertFalse(myAction.validBuild(w, notValidBuild));
        assertTrue(myAction.validBuild(w, secondBuild));
    }
}
