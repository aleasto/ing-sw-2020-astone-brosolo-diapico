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
     * @param w    the worker
     * @param from the destination tile
     * @return true if the move resulted into a win
     */
    @Override
    public boolean postMove(Worker w, Tile from) {
        return decorated.postMove(w, from);
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
     * Checks if the given build valid
     *
     * @param w  the worker
     * @param to the destination tile
     * @return true if `w` can built in `to`
     */
    @Override
    public boolean validBuild(Worker w, Tile to) {
        return decorated.validBuild(w, to);
    }

    /**
     * Action to be taken after having built
     *
     * @param w    the worker
     * @param to   the destination tile
     */
    @Override
    public void postBuild(Worker w, Tile to) {
        decorated.postBuild(w, to);
    }

    /**
     * Gets the last move the caller has completed.
     *
     * @return a Pair object representing the move from first to second
     */
    @Override
    public Pair<Tile> getLastMove() {
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
