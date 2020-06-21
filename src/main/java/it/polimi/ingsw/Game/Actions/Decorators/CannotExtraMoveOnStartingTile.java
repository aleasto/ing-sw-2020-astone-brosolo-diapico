package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CannotExtraMoveOnStartingTile extends ActionsDecorator {

    public CannotExtraMoveOnStartingTile(Actions decorated) {
        super(decorated);
    }

    @Override
    public boolean validMove(Worker w, Tile to) {
        // If we've already moved once, make sure we're not returning to the original location
        if (getHasMoved() && to.equals(getLastMove().getFirst()))
            return false;
        return super.validMove(w, to);
    }
}
