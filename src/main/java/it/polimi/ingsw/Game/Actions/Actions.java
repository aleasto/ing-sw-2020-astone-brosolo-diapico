package it.polimi.ingsw.Game.Actions;

import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.Game.Worker;

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
     * Is the caller forced to move this turn
     *
     * @return true if must move
     */
    public boolean mustMove();

    /**
     * Is the given move valid
     *
     * @param w  the worker
     * @param to the destination tile
     * @return true if `w` can move into `to`
     */
    public boolean validMove(Worker w, Tile to);

    /**
     * Perform a Move action and all its related behaviours
     *
     * @param w    the worker
     * @param to the destination tile
     * @return true if the move resulted into a win
     */
    public boolean doMove(Worker w, Tile to);

    /**
     * Is the caller allowed to perform a build action
     *
     * @return true if can build
     */
    public boolean canBuild();

    /**
     * Is the caller forced to build this turn
     *
     * @return true if must build
     */
    public boolean mustBuild();

    /**
     * Is the given build valid
     *
     * @param w  the worker
     * @param to the destination tile
     * @return true if `w` can built in `to`
     */
    public boolean validBuild(Worker w, Tile to, int level);

    /**
     * Perform a Build action and all its related behaviours
     *
     * @param w  the worker
     * @param to the destination tile
     * @param level the block level to build. starts from zero
     */
    public void doBuild(Worker w, Tile to, int level);

    /**
     * Get the last move the caller has completed.
     *
     * @return a Pair object representing the move from first to second
     */
    public Pair<Tile, Tile> getLastMove();

    /**
     * Get the last build the caller completed
     *
     * @return the last tile where it was built
     */
    public Tile getLastBuild();
}
