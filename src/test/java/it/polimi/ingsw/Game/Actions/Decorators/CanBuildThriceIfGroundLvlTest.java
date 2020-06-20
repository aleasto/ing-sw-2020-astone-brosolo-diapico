package it.polimi.ingsw.Game.Actions.Decorators;


import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.BaseActions;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanBuildThriceIfGroundLvlTest {

    @Test
    void buildUnmoved() {

        Actions myAction = new BaseActions();
        myAction = new CanBuildThriceIfGroundLvl(myAction);

        Board board = new Board(5, 5, 3);
        Tile startingTile = board.getAt(3, 3);
        Tile firstBuild = board.getAt(2, 2);
        Player me = new Player("", 0);
        Worker w = new Worker(me, startingTile);

        //Second build preparations
        Tile secondStart = board.getAt(3, 2);
        secondStart.buildUp();
        Worker secondW = new Worker(me, secondStart);

        //Third build preparations
        Tile thirdStart = board.getAt(0,0);
        Tile thirdBuild = board.getAt(0,1);
        Worker thirdW = new Worker(me, thirdStart);

        //Do the first build
        myAction.doBuild(w, firstBuild, firstBuild.getHeight());

        //Assert that I can build but not with the same worker
        assertTrue(myAction.canBuild());
        assertFalse(myAction.canUseThisWorkerNow(w));

        //Control that I can't build with a worker not on ground level
        assertFalse(myAction.canUseThisWorkerNow(secondW));

        //Now check that I can actually build with a ground level worker
        assertTrue(myAction.canUseThisWorkerNow(thirdW));

        //Bonus builds and stop building after three times
        myAction.doBuild(thirdW, thirdBuild, thirdBuild.getHeight());
        assertTrue(myAction.canUseThisWorkerNow(thirdW));
        myAction.doBuild(thirdW, thirdBuild, thirdBuild.getHeight());
        assertTrue(myAction.canUseThisWorkerNow(thirdW));
        myAction.doBuild(thirdW, thirdBuild, thirdBuild.getHeight());
        assertFalse(myAction.canBuild());
    }
}
