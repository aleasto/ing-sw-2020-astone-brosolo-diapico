package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CanBuildTwice extends ActionsDecorator {
    private int timesBuilt = 0;

    public CanBuildTwice(Actions decorated) {
        super(decorated);
    }

    @Override
    public void beginTurn() {
        timesBuilt = 0;
        super.beginTurn();
    }

    @Override
    public boolean canBuild() {
        //Checks if you have already built once
        if (getHasBuilt() && timesBuilt == 1) {
            return true;
        }
        //Asks super otherwise, this prevents to allow a build without a move
        return super.canBuild();
    }

    @Override
    public void doBuild(Worker w, Tile to, int level) {
        timesBuilt++;
        super.doBuild(w, to, level);
    }
}
