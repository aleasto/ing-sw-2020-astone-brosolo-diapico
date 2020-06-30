package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CanHeadbutt extends ActionsDecorator {
    private Board board;
    private Tile pushedTile;

    public CanHeadbutt(Actions decorated) {
        super(decorated);
    }

    @Override
    public void beginTurn() {
        pushedTile = null;
        board = null;
        super.beginTurn();
    }

    @Override
    public boolean validMove(Worker w, Tile to) {
        //We get the coordinates of where the enemy will be pushed
        int x = to.getX() + (to.getX() - w.getTile().getX());
        int y = to.getY() + (to.getY() - w.getTile().getY());

        //We retrieve the tile
        board = to.getBoard();
        try {
            pushedTile = board.getAt(x, y);
        } catch (IndexOutOfBoundsException ex){
            return to.isEmpty() && super.validMove(w, to);
        }

        return (to.isEmpty() || (pushedTile.isEmpty() && !pushedTile.hasDome())) && super.validMove(w, to);
    }

    @Override
    public boolean doMove(Worker w, Tile to) {
        Worker pushedWorker = to.getOccupant();

        //Do the move and save the result
        boolean win = super.doMove(w, to);

        //Push the enemy
        if (pushedWorker != null && pushedTile != null) {
            pushedWorker.setTile(pushedTile);
            pushedTile.setOccupant(pushedWorker);
        }
        return win;
    }
}
