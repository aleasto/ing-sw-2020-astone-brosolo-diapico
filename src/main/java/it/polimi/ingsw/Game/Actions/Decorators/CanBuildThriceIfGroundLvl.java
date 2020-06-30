package it.polimi.ingsw.Game.Actions.Decorators;

import it.polimi.ingsw.Game.Actions.Actions;
import it.polimi.ingsw.Game.Actions.ActionsDecorator;
import it.polimi.ingsw.Game.Board;
import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Game.Worker;

import java.util.ArrayList;
import java.util.List;

public class CanBuildThriceIfGroundLvl extends ActionsDecorator {
    private int bonusBuilds = 0;
    private Worker movingWorker;
    private Worker bonusWorker;
    private List<Worker> groundWorkers = new ArrayList<>();

    public CanBuildThriceIfGroundLvl(Actions decorated) {
        super(decorated);
    }

    @Override
    public void beginTurn() {
        bonusBuilds = 0;
        movingWorker = null;
        bonusWorker = null;
        groundWorkers.clear();
        super.beginTurn();
    }

    @Override
    public boolean canBuild() {
        if(getHasBuilt() && (bonusBuilds < 3)) {
            return true;
        }

        return super.canBuild();
    }

    @Override
    public boolean canUseThisWorkerNow(Worker w) {
        if (!getHasBuilt()) {
            return super.canUseThisWorkerNow(w);
        }

        // For my extra builds I can choose any ground worker, but not the worker I used to move.
        //Once I chose a bonus worker I have to keep using him
        if(bonusWorker == null) {
            return !w.equals(movingWorker) && groundWorkers.contains(w);
        } else {
            return w.equals(bonusWorker);
        }

    }

    @Override
    public void doBuild(Worker w, Tile to, int level) {
        if(!getHasBuilt()) {
            movingWorker = w;
            groundWorkers = getGroundWorkersOf(w.getOwner(), to.getBoard());
        } else {
            bonusBuilds++;
            bonusWorker = w;
        }

        super.doBuild(w, to, level);
    }

    private List<Worker> getGroundWorkersOf(Player player, Board board) {
        List<Worker> workers = new ArrayList<>();
        for (int i = 0; i < board.getDimX(); i++) {
            for (int j = 0; j < board.getDimY(); j++) {
                Tile tile = board.getAt(i, j);
                Worker w = tile.getOccupant();
                if (w != null && player.equals(w.getOwner()) && !movingWorker.equals(w) && tile.getHeight() == 0) {
                    workers.add(w);
                }
            }
        }
        return workers;
    }
}
