package it.polimi.ingsw.Actions;

import it.polimi.ingsw.Tile;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.Worker;

public interface Actions {

    /**
     * To be called whenever the caller's turn began.
     * Used for initialization purposes.
     */
    public void beginTurn();

    /**
     * Is the caller allowed to perform a move
     *
     * @return true if can move action
     */
    public boolean canMove();

    /**
     * Is the given move valid
     *
     * @param w  the worker
     * @param to the destination tile
     * @return true if `w` can move into `to`
     */
    public boolean validMove(Worker w, Tile to);

    /**
     * Action to be taken after having moved
     *
     * @param w    the worker
     * @param from the source tile
     * @return true if the move resulted into a win
     */
    public boolean postMove(Worker w, Tile from);

    /**
     * Is the caller allowed to perform a build action
     *
     * @return true if can build
     */
    public boolean canBuild();

    /**
     * Is the given build valid
     *
     * @param w  the worker
     * @param to the destination tile
     * @return true if `w` can built in `to`
     */
    public boolean validBuild(Worker w, Tile to);

    /**
     * Action to be taken after having built
     *
     * @param w  the worker
     * @param to the destination tile
     */
    public void postBuild(Worker w, Tile to);

    /**
     * Get the last move the caller has completed.
     *
     * @return a Pair object representing the move from first to second
     */
    public Pair<Tile> getLastMove();
}
