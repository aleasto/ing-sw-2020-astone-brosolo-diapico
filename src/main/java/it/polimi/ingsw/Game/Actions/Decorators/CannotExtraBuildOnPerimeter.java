package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CannotExtraBuildOnPerimeter extends ActionsDecorator {

    public CannotExtraBuildOnPerimeter(Actions decorated) {
        super(decorated);
    }

    @Override
    public boolean validBuild(Worker w, Tile to, int level) {
        Board board = to.getBoard();
        boolean isPerimeter = to.getY() == 0 || to.getX() == 0 || to.getY() == (board.getDimY() - 1) || to.getX() == (board.getDimX() - 1);

        //If we've already built once, make sure we're not building on the perimeter
        if(getHasBuilt() && isPerimeter) {
            return false;
        }
        return super.validBuild(w, to, level);
    }
}
