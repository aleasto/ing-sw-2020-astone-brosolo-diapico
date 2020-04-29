package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CanBuildBeforeMove extends ActionsDecorator {
    private boolean hasBuiltBeforeMoving = false;
    private boolean hasBuiltAfter = false;
    private boolean hasMoved = false;

    public CanBuildBeforeMove (Actions decorated) {
        super(decorated);
    }

    @Override
    public void beginTurn() {
        hasBuiltBeforeMoving  = false;
        hasBuiltAfter = false;
        hasMoved = false;

        super.beginTurn();
    }

    @Override
    public boolean canBuild() {
        if (!hasBuiltBeforeMoving && hasMoved) {
            // This is a normal sequence of actions, so ask super
            return super.canBuild();
        }
        if (hasBuiltBeforeMoving && hasMoved && !hasBuiltAfter) {
            return true;
        }
        return false;
    }

    @Override
    public boolean doMove(Worker w, Tile to) {
        hasMoved = true;
        return super.doMove(w, to);
    }

    @Override
    public void doBuild(Worker w, Tile to, int level) {
        if (!hasMoved) {
            hasBuiltBeforeMoving = true;
        } else {
            hasBuiltAfter = true;
        }
        super.doBuild(w, to, level);
    }

    @Override
    public boolean validMove(Worker w, Tile to) {
        if (hasBuiltBeforeMoving){
            Tile from = w.getTile();
            if(to.getHeight() > from.getHeight()) {
                return false;
            }
        }
        return super.validMove(w, to);
    }

}
