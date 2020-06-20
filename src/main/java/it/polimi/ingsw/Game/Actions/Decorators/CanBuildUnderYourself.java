package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

public class CanBuildUnderYourself extends ActionsDecorator {

    public CanBuildUnderYourself(Actions decorated) {
        super(decorated);
    }

    @Override
    public boolean validBuild(Worker w, Tile to, int level) {
        Board board = to.getBoard();
        return super.validBuild(w, to, level) || (to.equals(w.getTile()) && level == to.getHeight() && to.getHeight() < board.getMaxHeight());
    }
}
