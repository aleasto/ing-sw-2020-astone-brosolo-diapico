package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanBuildTwoTimesTest {

    @Test
    void canBuildTwoTimes() {
        Actions myAction = new BaseActions();
        myAction = new CanBuildTwoTimes(myAction);

        Board board = new Board(5, 5, 3);
        Tile startingTile = board.getAt(0, 0);
        Tile correctBuild = board.getAt(1, 1);
        Worker w = new Worker(new Player("", 0), startingTile);

        myAction.doBuild(w, correctBuild, correctBuild.getHeight());
        assertTrue(myAction.canBuild());

        myAction.doBuild(w, correctBuild, correctBuild.getHeight());
        assertFalse(myAction.canBuild());
    }
}
