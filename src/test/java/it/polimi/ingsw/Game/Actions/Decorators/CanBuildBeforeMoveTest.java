package it.polimi.ingsw.Game.Actions.Decorators;

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

    @Test
    void validMove (){
        Actions myAction = new BaseActions();
        myAction = new CanBuildBeforeMove(myAction);

        Board board = new Board();
        Tile startingTile = board.getAt(3, 3);
        Tile bonusBuild = board.getAt(2, 3);
        Worker w = new Worker(new Player(), startingTile);

        myAction.doBuild(w, bonusBuild, bonusBuild.getHeight());
        assertFalse(myAction.validMove(w, bonusBuild));
    }

    @Test
    void canBuild (){
        Actions myAction = new BaseActions();
        myAction = new CanBuildBeforeMove(myAction);

        Board board = new Board();
        Tile startingTile = board.getAt(3, 3);
        Tile move = board.getAt(2,2);
        Tile build = board.getAt(2, 3);
        Worker w = new Worker(new Player(), startingTile);

        myAction.doBuild(w, build , build.getHeight());
        // We need to move before building again
        assertFalse(myAction.canBuild());

        myAction.doMove(w, move);
        assertTrue(myAction.canBuild());

        myAction.doBuild(w, build , build.getHeight());
        // We can't build twice after
        assertFalse(myAction.canBuild());
    }

    @Test
    void normalCanBuild() {
        Actions myAction = new BaseActions();
        myAction = new CanBuildBeforeMove(myAction);

        Board board = new Board();
        Tile startingTile = board.getAt(3, 3);
        Tile move = board.getAt(2,2);
        Tile build = board.getAt(2, 3);
        Worker w = new Worker(new Player(), startingTile);

        myAction.doMove(w, move);
        assertTrue(myAction.canBuild());

        myAction.doBuild(w, build , build.getHeight());
        // We can't build twice after
        assertFalse(myAction.canBuild());
    }
}