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
        if (level == to.getBoard().getMaxHeight()) {
            // Check if we could place a normal block. If so, we can place a dome too.
            return super.validBuild(w, to, to.getHeight());
        }
        return super.validBuild(w, to, level);
    }
}
