package it.polimi.ingsw.Game;

import java.io.Serializable;

public class Worker implements Serializable, Cloneable {
    private Tile tile;
    private Player owner;

    /**
     * Create a worker object
     * @param owner the player who owns this worker
     * @param startingTile where to initially place this worker
     */
    public Worker(Player owner, Tile startingTile) {
        this.tile = startingTile;
        this.owner = owner;

        startingTile.setOccupant(this);
    }

    public Player getOwner() {
        return owner;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    /**
     * Create a deep clone of this object
     * @return a new worker that is owned by the same player as this, but placed on no tile.
     * @throws CloneNotSupportedException if any field is not cloneable
     */
    @Override
    public Worker clone() throws CloneNotSupportedException {
        Worker clone = (Worker) super.clone();
        clone.tile = null;
        clone.owner = this.owner.clone();
        return clone;
    }
}
