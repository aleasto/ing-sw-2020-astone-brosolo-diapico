package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.EnemyActionsDecorator;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CannotWinOnPerimeter extends EnemyActionsDecorator {

    public CannotWinOnPerimeter(Actions decorated, Actions enemy) {
        super(decorated, enemy);
    }

    @Override
    public boolean doMove(Worker w, Tile to) {
        if (getEnemy().hasLost()) {
            // This effect is no longer relevant as the player which caused it is no longer playing
            return super.validMove(w, to);
        }

        Board board = to.getBoard();
        boolean isPerimeter = to.getY() == 0 || to.getX() == 0 || to.getY() == (board.getDimY() - 1) || to.getX() == (board.getDimX() - 1);

        return super.doMove(w, to) && !isPerimeter;
    }
}
