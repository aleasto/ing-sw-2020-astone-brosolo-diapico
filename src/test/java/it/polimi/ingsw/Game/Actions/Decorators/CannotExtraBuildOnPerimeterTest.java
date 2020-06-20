package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class CannotExtraBuildOnPerimeterTest {

    @Test
    void cannotBuildOnPerimeter() {
        Actions myAction = new BaseActions();
        myAction = new CanBuildTwoTimes(myAction);
        myAction = new CannotExtraBuildOnPerimeter(myAction);

        Board board = new Board(5, 5, 3);
        Tile startingTile = board.getAt(0, 0);
        Tile correctBuild = board.getAt(1, 1);
        Tile incorrectBuild = board.getAt(0, 1);
        Worker w = new Worker(new Player("", 0), startingTile);

        myAction.doBuild(w, correctBuild, correctBuild.getHeight());
        assertTrue(myAction.canBuild());

        assertFalse(myAction.validBuild(w, incorrectBuild, incorrectBuild.getHeight()));

        assertTrue(myAction.validBuild(w, correctBuild, correctBuild.getHeight()));
        myAction.doBuild(w, correctBuild, correctBuild.getHeight());

        assertFalse(myAction.canBuild());
    }
}
