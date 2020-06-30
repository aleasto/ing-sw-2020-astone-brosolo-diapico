package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CanInteractWithEnemy extends ActionsDecorator {

    public CanInteractWithEnemy(Actions decorated) {
        super(decorated);
    }

    @Override
    public boolean validMove(Worker w, Tile to) {
        if (!to.isEmpty() && !w.getOwner().equals(to.getOccupant().getOwner())) {
            Tile fake = new Tile(to.getBoard(), to.getX(), to.getY());
            for (int i = 0; i < to.getHeight(); i++)
                fake.buildUp();

            return super.validMove(w, fake);
        }
        return super.validMove(w, to);
    }
}
