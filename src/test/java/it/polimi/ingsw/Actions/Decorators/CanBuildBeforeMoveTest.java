package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Actions.Decorators.CanBuildBeforeMove;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanBuildBeforeMoveTest {

    //Test that you can't move up if you built before
    @Test
    void validMove (){
        Actions myAction = new BaseActions();
        myAction = new CanBuildBeforeMove(myAction);

        Board board = new Board();
        Tile startingTile = board.getAt(3, 3);
        Tile bonusBuild = board.getAt(2, 3);
        Worker w = new Worker(new Player(), startingTile);

        myAction.doBuild(w, bonusBuild, bonusBuild.getHeight());
        assertTrue(myAction.validMove(w, bonusBuild));

        myAction.doMove(w, bonusBuild);
        assertFalse(myAction.validMove(w, bonusBuild));

    }


    //Test that you can't move and build twice
    @Test
    void canBuild (){
        Actions myAction = new BaseActions();
        myAction = new CanBuildBeforeMove(myAction);

        Board board = new Board();
        Tile startingTile = board.getAt(3, 3);
        Tile moveTile = board.getAt(2,3);
        Tile bonusBuild = board.getAt(2, 2);
        Tile build = board.getAt(2, 3);
        Worker w = new Worker(new Player(), startingTile);

        myAction.doMove(w, moveTile);
        assertTrue(myAction.canBuild());

        myAction.doBuild(w, bonusBuild, bonusBuild.getHeight());
        assertTrue(myAction.canBuild());

        myAction.doBuild(w, build , build.getHeight());
        assertFalse(myAction.canBuild());

    }

}