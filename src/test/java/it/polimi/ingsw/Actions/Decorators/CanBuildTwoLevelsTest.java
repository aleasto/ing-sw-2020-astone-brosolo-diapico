package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Actions.Decorators.CanBuildTwoLevels;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanBuildTwoLevelsTest {

    //Tests that you can perform the build action twice on the same level
    @Test
    void canBuild() {
        Actions myAction = new BaseActions();
        myAction = new CanBuildTwoLevels(myAction);

        Tile startingTile = new Tile(3, 3);
        Tile correctBuild = new Tile(2, 2);
        Worker w = new Worker(new Player(), startingTile);

        myAction.postBuild(w, correctBuild);
        assertTrue(myAction.canBuild());

        myAction.postBuild(w, correctBuild);
        assertFalse(myAction.canBuild());
    }

    //Tests that you can build two levels on the same tile and not on any other tile
    //Also tests that you can't build after the two scheduled builds
    @Test
    void validBuild() {
        Actions myAction = new BaseActions();
        myAction = new CanBuildTwoLevels(myAction);

        Tile startingTile = new Tile(3, 3);
        Tile movingTile = new Tile(3, 3);
        Tile correctBuild = new Tile(2, 2);
        Tile incorrectBuild = new Tile(2, 3);
        Worker w = new Worker(new Player(), startingTile);

        //Fakes the movement and asserts that you can build
        w.setTile(movingTile);
        myAction.postMove(w, startingTile);
        assertTrue(myAction.canBuild());

        //Performs the firts build
        assertTrue(myAction.validBuild(w, correctBuild));
        myAction.postBuild(w, correctBuild);

        //Checks that you can only build on the same tile as the last one
        assertFalse(myAction.validBuild(w, incorrectBuild));
        assertTrue(myAction.validBuild(w, correctBuild));
        myAction.postBuild(w, correctBuild);

        //Checks that you can't build afterwards
        assertFalse(myAction.canBuild());
    }
}
