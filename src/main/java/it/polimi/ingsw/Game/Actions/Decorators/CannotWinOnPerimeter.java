package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CannotWinOnPerimeter extends ActionsDecorator {
    private Actions enemy; // This will basically hold a reference to Hera, the enemy god that imposed us this malus

    public CannotWinOnPerimeter(Actions decorated, Actions enemy) {
        super(decorated);
        this.enemy = enemy;
    }

    @Override
    public boolean doMove(Worker w, Tile to) {
        if (enemy.hasLost()) {
            // This effect is no longer relevant as the player which caused it is no longer playing
            return super.validMove(w, to);
        }

        Board board = to.getBoard();
        boolean isPerimeter = to.getY() == 0 || to.getX() == 0 || to.getY() == (board.getDimY() - 1) || to.getX() == (board.getDimX() - 1);

        return super.doMove(w, to) && !isPerimeter;
    }
}
