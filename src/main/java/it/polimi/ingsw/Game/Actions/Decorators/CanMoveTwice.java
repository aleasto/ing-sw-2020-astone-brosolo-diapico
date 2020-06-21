package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CanMoveTwice extends ActionsDecorator {
    private int timesMoved = 0;

    public CanMoveTwice(Actions decorated) {
        super(decorated);
    }

    @Override
    public void beginTurn() {
        timesMoved = 0;

        // And call super, which will reset their counters.
        super.beginTurn();
    }

    @Override
    public boolean canMove() {
        if (timesMoved == 1 && canBuild())
            return true;

        return super.canMove();
    }

    @Override
    public boolean doMove(Worker w, Tile to) {
        timesMoved++;

        // We do not change the win condition, so trust super to judge that.
        return super.doMove(w, to);
    }
}
