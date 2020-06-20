package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CanExtraBuildALevel extends ActionsDecorator {

    public CanExtraBuildALevel(Actions decorated) {
        super(decorated);
    }

    @Override
    public boolean validBuild(Worker w, Tile to, int level) {
        //If we've already built once, make sure we're building on the same tile but not a Dome
        if (getHasBuilt() && (!to.equals(getLastBuild()) || to.getHeight() == to.getBoard().getMaxHeight())) {
            return false;
        }
        return super.validBuild(w, to, level);
    }
}
