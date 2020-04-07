package it.polimi.ingsw.Actions.Decorators;

import it.polimi.ingsw.Actions.Actions;
import it.polimi.ingsw.Actions.ActionsDecorator;
import it.polimi.ingsw.Tile;
import it.polimi.ingsw.Worker;

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
    public boolean validBuild(Worker w, Tile to) {
        //If we've already built once, make sure we're building on the same tile
        if(timesBuilt==1 && !to.equals(getLastBuild())) {
            return false;
        }
        return super.validBuild(w, to);
    }

    @Override
    public void postBuild(Worker w, Tile to) {
        //Increments the counter and calls super
        timesBuilt++;

        super.postBuild(w, to);
    }
}
