package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CanBuildTwoLevels extends ActionsDecorator {
    private int timesBuilt = 0;

    public CanBuildTwoLevels(Actions decorated) {
        super(decorated);
    }

    @Override
    public void beginTurn() {
        //Sets the number of times you built at the start of each turn
        timesBuilt = 0;
        super.beginTurn();
    }

    @Override
    public boolean canBuild() {
        //Checks if you have already built once
        if (timesBuilt == 1) {
            return true;
        }
        //Asks super otherwise, this prevents to allow a build without a move
        return super.canBuild();
    }

    @Override
    public boolean validBuild(Worker w, Tile to, int level) {
        //If we've already built once, make sure we're building on the same tile
        if(timesBuilt==1 && !to.equals(getLastBuild())) {
            return false;
        }
        return super.validBuild(w, to, level);
    }

    @Override
    public void doBuild(Worker w, Tile to, int level) {
        //Increments the counter and calls super
        timesBuilt++;

        super.doBuild(w, to, level);
    }
}
