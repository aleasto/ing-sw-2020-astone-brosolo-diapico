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
    public boolean validMove(Worker w, Tile to) {
        // Relaxing just 1 constrain of super.validMove() is not an easy task, as it checks all together.
        // If the destination tile is occupied by an enemy, pretend that is empty instead and ask super
        // by constructing a fake tile object.
        if (!to.isEmpty() && !w.getOwner().equals(to.getOccupant().getOwner())) {
            // Since we will pass in a fake tile, we can't rely on super to check if src and dst are different.
            // Actually now we can, Tile::equals checks for (x, y)
            // if (to == w.getTile())
            //    return false;

            Tile fake = new Tile(to.getBoard(), to.getX(), to.getY());
            for (int i = 0; i < to.getHeight(); i++)
                fake.buildUp();

            return super.validMove(w, fake);
        }
        return super.validMove(w, to);
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
