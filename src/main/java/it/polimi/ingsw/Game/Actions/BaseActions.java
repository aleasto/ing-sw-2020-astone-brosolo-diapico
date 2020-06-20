package it.polimi.ingsw.Game.Actions;

import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.Game.Worker;

public class BaseActions implements Actions {
    private boolean hasMoved = false;
    private boolean hasBuilt = false;
    private boolean hasLost = false;
    private Pair<Tile, Tile> lastMove = null;
    private Tile lastBuild = null;
    private Worker usedWorker = null;

    /**
     * To be called whenever the caller's turn began.
     * Used for initialization purposes.
     */
    @Override
    public void beginTurn() {
        hasMoved = false;
        hasBuilt = false;
        usedWorker = null;
    }

    /**
     * Is the caller allowed to perform a move
     *
     * @return true if can move action
     */
    @Override
    public boolean canMove() {
        return !hasMoved;
    }

    /**
     * Is the caller forced to move this turn
     *
     * @return true if must move
     */
    @Override
    public boolean mustMove() {
        return !hasMoved;
    }

    /**
     * Is the given move valid
     *
     * @param w  the worker
     * @param to the destination tile
     * @return true if `w` can move into `to`
     */
    @Override
    public boolean validMove(Worker w, Tile to) {
        Tile from = w.getTile();
        boolean x_ok = Math.abs(from.getX() - to.getX()) <= 1;
        boolean y_ok = Math.abs(from.getY() - to.getY()) <= 1;
        boolean z_ok = (to.getHeight() <= from.getHeight() ||     // Can always go down
                        to.getHeight() == from.getHeight() + 1);   // or 1 up

        return to.isEmpty() && !to.hasDome() && x_ok && y_ok && z_ok && !to.equals(from);
    }

    /**
     * Action to be taken after having moved
     *
     * @param w  the worker
     * @param to the destination tile
     * @return true if the move resulted into a win
     */
    @Override
    public boolean doMove(Worker w, Tile to) {
        Tile from = w.getTile();
        from.setOccupant(null);
        to.setOccupant(w);
        w.setTile(to);
        hasMoved = true;
        lastMove = new Pair<>(from, to);
        usedWorker = w;
        return to.isWinLevel() && to.getHeight() > from.getHeight();
    }

    /**
     * Is the caller allowed to perform a build action
     *
     * @return true if can build
     */
    @Override
    public boolean canBuild() {
        return hasMoved && !hasBuilt;
    }

    /**
     * Is the caller forced to build this turn
     *
     * @return true if must build
     */
    @Override
    public boolean mustBuild() {
        return !hasBuilt;
    }

    /**
     * Is the given build valid
     *
     * @param w  the worker
     * @param to the destination tile
     * @return true if `w` can built in `to`
     */
    @Override
    public boolean validBuild(Worker w, Tile to, int level) {
        Tile from = w.getTile();
        boolean x_ok = Math.abs(from.getX() - to.getX()) <= 1;
        boolean y_ok = Math.abs(from.getY() - to.getY()) <= 1;
        boolean level_ok = to.getHeight() == level;     // We build a lvl0 block on the ground floor, etc.
        return to.isEmpty() && !to.hasDome() && x_ok && y_ok && level_ok && !to.equals(from);
    }

    /**
     * Action to be taken after having built
     *
     * @param w  the worker
     * @param to the destination tile
     */
    @Override
    public void doBuild(Worker w, Tile to, int level) {

        //Could be replace with simply a to.buildUp() as the method will verify what to build
        if (level != to.getBoard().getMaxHeight()) {
            to.buildUp();
        } else {
            to.buildDome();
        }
        hasBuilt = true;
        lastBuild = to;
        usedWorker = w;
    }

    /**
     * Get the last move the caller has completed.
     *
     * @return a Pair object representing the move from first to second
     */
    @Override
    public Pair<Tile, Tile> getLastMove() {
        return lastMove;
    }

    @Override
    public Tile getLastBuild() {
        return lastBuild;
    }

    /**
     * Actions to be taken when hen the caller loses
     */
    @Override
    public void onLose() {
        // This player is no longer in the game
        hasLost = true;
    }

    /**
     * Has the caller lost
     *
     * @return true if this player has lost
     */
    @Override
    public boolean hasLost() {
        return hasLost;
    }

    /**
     * Check if the player can perform an action with a specific worker
     *
     * @param w the worker
     * @return true if the player can use the worker for my next move
     */
    @Override
    public boolean canUseThisWorkerNow(Worker w) {
        return usedWorker == null || usedWorker.equals(w);
    }
}
