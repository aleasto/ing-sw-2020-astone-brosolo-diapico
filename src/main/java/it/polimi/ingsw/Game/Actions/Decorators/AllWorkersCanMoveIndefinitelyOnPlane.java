package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class AllWorkersCanMoveIndefinitelyOnPlane extends ActionsDecorator {
    public AllWorkersCanMoveIndefinitelyOnPlane(Actions decorated) {
        super(decorated);
    }

    @Override
    public boolean canMove() {
        if (getHasBuilt())
            return false;   // Base Actions would allow a move after a build

        if (!getHasMoved())
            return super.canMove();

        // We can keep moving as long as we're moving on a plane
        return movingOnPlane();
    }

    @Override
    public boolean mustMove() {
        return false;
    }

    @Override
    public boolean validMove(Worker w, Tile to) {
        if (!getHasMoved() || (getHasMoved() && !movingOnPlane()))
            return super.validMove(w, to);

        return super.validMove(w, to) && w.getTile().getHeight() == to.getHeight();
    }

    @Override
    public boolean canUseThisWorkerNow(Worker w) {
        if (!getHasMoved() || (getHasMoved() && !movingOnPlane())) {
            return super.canUseThisWorkerNow(w);
        }

        return !getHasBuilt() || movingOnPlane();
    }

    @Override
    public boolean canBuild() {
        return !getHasBuilt() || super.canBuild();
    }

    private boolean movingOnPlane() {
        return getLastMove().getFirst().getHeight() == getLastMove().getSecond().getHeight();
    }
}
