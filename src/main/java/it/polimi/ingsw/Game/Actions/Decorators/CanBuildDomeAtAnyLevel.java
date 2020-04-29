package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CanBuildDomeAtAnyLevel extends ActionsDecorator {

    public CanBuildDomeAtAnyLevel(Actions decorated) {
        super(decorated);
    }

    @Override
    public boolean validBuild(Worker w, Tile to, int level) {
        Tile from = w.getTile();
        boolean x_ok = Math.abs(from.getX() - to.getX()) <= 1;
        boolean y_ok = Math.abs(from.getY() - to.getY()) <= 1;
        return x_ok && y_ok && ((Tile.getMaxHeight() == level) || (to.getHeight() == level)) && !to.equals(from) && !to.hasDome();
    }
}
