package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CanSwapWithEnemy extends ActionsDecorator {

    public CanSwapWithEnemy(Actions decorated) {
        super(decorated);
    }

    @Override
    public boolean doMove(Worker w, Tile to) {
        Tile from = w.getTile();
        Worker swapped = to.getOccupant();

        // Do the move and save the result
        boolean win = super.doMove(w, to);

        // Do the swap
        if (swapped != null) {
            swapped.setTile(from);
            from.setOccupant(swapped);
        }
        return win;
    }
}
