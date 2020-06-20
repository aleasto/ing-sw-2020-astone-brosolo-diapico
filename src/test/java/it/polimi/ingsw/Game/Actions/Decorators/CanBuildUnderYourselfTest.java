package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanBuildUnderYourselfTest {

    @Test
    void buildOnYourself() {
        Actions myAction = new BaseActions();
        myAction = new CanBuildUnderYourself(myAction);

        Board board = new Board(5, 5, 3);
        Tile startingTile = board.getAt(1, 1);
        Worker w = new Worker(new Player("", 0), startingTile);

        assertTrue(myAction.validBuild(w, startingTile, startingTile.getHeight()));

        for(int i=0; i<board.getMaxHeight(); i++) {
            startingTile.buildUp();
        }

        assertFalse(myAction.validBuild(w, startingTile, startingTile.getHeight()));
    }
}
