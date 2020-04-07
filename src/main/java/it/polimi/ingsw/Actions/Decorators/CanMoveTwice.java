package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Actions.Actions;
import it.polimi.ingsw.Actions.ActionsDecorator;
import it.polimi.ingsw.Tile;
import it.polimi.ingsw.Worker;

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
        if (timesMoved == 1)
            return true;

        // If we have tracked more than 2 moves, it does not necessarily mean we cannot move anymore.
        // The class we're decorating could be less restrictive. Let's pretend we're decorating a "CanMoveThrice"
        // object. In that case we must give the last say to that class, thus we ask super.
        return super.canMove();
    }

    @Override
    public boolean validMove(Worker w, Tile to) {
        // If we've already moved once, make sure we're not returning to the original location
        if (timesMoved == 1 && to == getLastMove().getFirst())
            return false;
        return super.validMove(w, to);
    }

    @Override
    public boolean postMove(Worker w, Tile from) {
        timesMoved++;

        // We do not change the win condition, so trust super to judge that.
        return super.postMove(w, from);
    }
}
