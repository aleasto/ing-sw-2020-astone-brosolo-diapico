package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CanBuildBeforeMove extends ActionsDecorator {
    private boolean HasBuiltBeforeMoving = false;
    private boolean HasMoved = false;

    public CanBuildBeforeMove (Actions decorated) {
        super(decorated);
    }

    @Override
    public void beginTurn() {
        HasBuiltBeforeMoving  = false;
        HasMoved = false;

        super.beginTurn();
    }

    @Override
    public boolean canBuild() {
        if (HasMoved) {
            return super.canBuild();
        }
        if (!HasBuiltBeforeMoving) {
            return true;
        }
        return false;
    }

    @Override
    public boolean doMove(Worker w, Tile to) {
        HasMoved = true;
        return super.doMove(w, to);
    }

    @Override
    public void doBuild(Worker w, Tile to, int level) {
        if(!HasMoved) {
            HasBuiltBeforeMoving = true;
        }
        super.doBuild(w, to, level);
    }

    @Override
    public boolean validMove(Worker w, Tile to) {
        if(HasBuiltBeforeMoving){
            Tile from = w.getTile();
            if(to.getHeight() > from.getHeight()) {
                return false;
            }

        }
        return super.validMove(w, to);
    }

}
