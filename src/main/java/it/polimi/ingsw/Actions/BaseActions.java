package it.polimi.ingsw.Actions;

import it.polimi.ingsw.Tile;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.Worker;

public class BaseActions implements Actions {
    private boolean hasMoved = false;
    private boolean hasBuilt = false;
    private Pair<Tile> lastMove;

    /**
     * To be called whenever the caller's turn began.
     * Used for initialization purposes.
     */
    @Override
    public void beginTurn() {
        hasMoved = false;
        hasBuilt = false;
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
        boolean z_ok = to.getHeight() <= from.getHeight() ||     // Can always go down
                       to.getHeight() == from.getHeight() + 1;   // or 1 up

        return x_ok && y_ok && z_ok && !to.equals(from);
    }

    /**
     * Action to be taken after having moved
     *
     * @param w    the worker
     * @param to   the destination tile
     * @return true if the move resulted into a win
     */
    @Override
    public boolean postMove(Worker w, Tile to) {
        hasMoved = true;
        lastMove = new Pair(w.getTile(), to);
        return to.isTopLevel();
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
     * Is the given build valid
     *
     * @param w  the worker
     * @param to the destination tile
     * @return true if `w` can built in `to`
     */
    @Override
    public boolean validBuild(Worker w, Tile to) {
        Tile from = w.getTile();
        boolean x_ok = Math.abs(from.getX() - to.getX()) <= 1;
        boolean y_ok = Math.abs(from.getY() - to.getY()) <= 1;

        return x_ok && y_ok && !to.equals(from);
    }

    /**
     * Action to be taken after having built
     *
     * @param w    the worker
     * @param to   the destination tile
     */
    @Override
    public void postBuild(Worker w, Tile to) {
        hasBuilt = true;
    }

    /**
     * Get the last move the caller has completed.
     *
     * @return a Pair object representing the move from first to second
     */
    @Override
    public Pair<Tile> getLastMove() {
        return lastMove;
    }
}
