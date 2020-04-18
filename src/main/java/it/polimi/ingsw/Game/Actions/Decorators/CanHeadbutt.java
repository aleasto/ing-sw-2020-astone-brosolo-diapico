package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CanHeadbutt extends ActionsDecorator {

    public CanHeadbutt(Actions decorated) {
        super(decorated);
    }

    @Override
    public void beginTurn() {
        super.beginTurn();
    }

    @Override
    public boolean validMove(Worker w, Tile to) {
        if (!to.isEmpty() && !w.getOwner().equals(to.getOccupant().getOwner())) {
            //We get the coordinates of where the enemy will be pushed
            int x = to.getX() + (to.getX() - w.getTile().getX());
            int y = to.getY() + (to.getY() - w.getTile().getY());

            //We retrieve the tile
            Board board = to.getBoard();
            Tile pushedTile = board.getAt(x, y);
            //If the tile is already occupied we can't perform an Headbutt action
            if (!pushedTile.isEmpty()) {
                return false;
            }

            //Else we build a fake tile with the same coordinates as the tile we'll be moving to in order to relieve the stress
            //under the super constraint of having an empty tile
            Tile fake = new Tile(board, to.getX(), to.getY());
            for (int i = 0; i < to.getHeight(); i++)
                fake.buildUp();

            return super.validMove(w, fake);
        }
        return super.validMove(w, to);
    }

    @Override
    public boolean doMove(Worker w, Tile to) {
        Actions pushedAction = to.getOccupant().getOwner().getActions();
        Worker pushedWorker = to.getOccupant();

        return super.doMove(w, to);
    }
}
