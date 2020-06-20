package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CannotExtraBuildOnSameTile extends ActionsDecorator {

    public CannotExtraBuildOnSameTile(Actions decorated) {
        super(decorated);
    }

    @Override
    public boolean validBuild(Worker w, Tile to, int level) {
        //If we've already built once, make sure we're not building on the same tile again
        if (getHasBuilt() && to.equals(getLastBuild())) {
            return false;
        }
        return super.validBuild(w, to, level);
    }
}
