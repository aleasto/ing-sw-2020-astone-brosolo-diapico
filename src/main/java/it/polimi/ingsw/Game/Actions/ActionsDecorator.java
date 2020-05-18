package it.polimi.ingsw.Game.Actions;

import it.polimi.ingsw.Game.Tile;
import it.polimi.ingsw.Utils.Pair;
import it.polimi.ingsw.Game.Worker;

public class ActionsDecorator implements Actions {
    private final Actions decorated;

    public ActionsDecorator(Actions decorated) {
        this.decorated = decorated;
    }

    /**
     * To be called whenever the caller's turn begins.
     * Used for initialization purposes.
     */
    @Override
    public void beginTurn() {
        this.decorated.beginTurn();
    }

    /**
     * Checks if the caller is allowed to perform a move
     *
     * @return true if can move action
     */
    @Override
    public boolean canMove() {
        return decorated.canMove();
    }

    /**
     * Is the caller forced to move this turn
     *
     * @return true if must move
     */
    @Override
    public boolean mustMove() {
        return decorated.mustMove();
    }

    /**
     * Checks if the given move is valid
     *
     * @param w  the worker
     * @param to the destination tile
     * @return true if `w` can move into `to`
     */
    @Override
    public boolean validMove(Worker w, Tile to) {
        return decorated.validMove(w, to);
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
        return decorated.doMove(w, to);
    }

    /**
     * Checks if the caller is allowed to perform a build action
     *
     * @return true if can build
     */
    @Override
    public boolean canBuild() {
        return decorated.canBuild();
    }

    /**
     * Is the caller forced to build this turn
     *
     * @return true if must build
     */
    @Override
    public boolean mustBuild() {
        return decorated.mustBuild();
    }

    /**
     * Checks if the given build valid
     *
     * @param w  the worker
     * @param to the destination tile
     * @return true if `w` can built in `to`
     */
    @Override
    public boolean validBuild(Worker w, Tile to, int level) {
        return decorated.validBuild(w, to, level);
    }

    /**
     * Perform a Build action and all its related behaviours
     *
     * @param w    the worker
     * @param to   the destination tile
     * @param level the block level to build. starts from zero
     */
    @Override
    public void doBuild(Worker w, Tile to, int level) {
        decorated.doBuild(w, to, level);
    }

    /**
     * Gets the last move the caller has completed.
     *
     * @return a Pair object representing the move from first to second
     */
    @Override
    public Pair<Tile, Tile> getLastMove() {
        return decorated.getLastMove();
    }

    /**
     * Gets the last build the caller has completed
     *
     * @return the Tile where it was previously built
     */
    @Override
    public Tile getLastBuild() {
        return decorated.getLastBuild();
    }
}
