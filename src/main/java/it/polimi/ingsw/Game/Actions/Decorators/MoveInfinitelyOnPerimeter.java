package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class MoveInfinitelyOnPerimeter extends ActionsDecorator {
    private boolean onPerimeter = false;

    public MoveInfinitelyOnPerimeter(Actions decorated) {
        super(decorated);
    }

    @Override
    public void beginTurn() {
        onPerimeter = false;
        super.beginTurn();
    }

    @Override
    public boolean canMove() {
        if(getHasMoved() && onPerimeter) {
            return true;
        }
        return super.canMove();
    }

    @Override
    public boolean doMove(Worker w, Tile to) {
        Board board = to.getBoard();
        onPerimeter = to.getY() == 0 || to.getX() == 0 || to.getY() == (board.getDimY() - 1) || to.getX() == (board.getDimX() - 1);

        return super.doMove(w, to);
    }
}
