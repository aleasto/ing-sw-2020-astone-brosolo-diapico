package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CanWinByDescending extends ActionsDecorator {
    private final static int MIN_DESCENDING_HEIGHT = 1;

    public CanWinByDescending(Actions decorated) {
        super(decorated);
    }

    @Override
    public boolean doMove(Worker w, Tile to) {
        Tile from = w.getTile();
        if (from.getHeight() - to.getHeight() > MIN_DESCENDING_HEIGHT) {
            super.doMove(w, to);
            return true;
        }
        return super.doMove(w, to);
    }
}
