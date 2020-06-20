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
    private boolean hasBuilt = false;
    private Worker movingWorker;
    private List<Worker> groundWorkers = new ArrayList<>();

    public CanBuildThriceIfGroundLvl(Actions decorated) {
        super(decorated);
    }

    @Override
    public void beginTurn() {
        bonusBuilds = 0;
        hasBuilt = false;
        movingWorker = null;
        groundWorkers.clear();
        super.beginTurn();
    }

    @Override
    public boolean canBuild() {
        if(hasBuilt && (bonusBuilds < 3)) {
            return true;
        }

        return super.canBuild();
    }

    @Override
    public boolean canUseThisWorkerNow(Worker w) {
        if (!hasBuilt) {
            return super.canUseThisWorkerNow(w);
        }

        // For my extra builds i can use any ground worker, but not the worker i used to move
        return !w.equals(movingWorker) && groundWorkers.contains(w);
    }

    @Override
    public void doBuild(Worker w, Tile to, int level) {
        if(!hasBuilt) {
            movingWorker = w;
            groundWorkers = getGroundWorkersOf(w.getOwner(), to.getBoard());
            hasBuilt = true;
        } else {
            bonusBuilds++;
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
