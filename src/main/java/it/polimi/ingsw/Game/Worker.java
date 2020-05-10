package it.polimi.ingsw.Game;

import java.io.Serializable;

public class Worker implements Serializable, Cloneable {
    private Tile tile;
    private Player owner;

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

    @Override
    public Worker clone() throws CloneNotSupportedException {
        Worker clone = (Worker) super.clone();
        clone.tile = null;
        clone.owner = this.owner.clone();
        return clone;
    }
}
