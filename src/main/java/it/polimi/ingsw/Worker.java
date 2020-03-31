package it.polimi.ingsw;

public class Worker {
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
}
