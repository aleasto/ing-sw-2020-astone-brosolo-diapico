package it.polimi.ingsw;

public class Worker {
    private Tile tile;

    public Worker(Tile startingTile) {
        tile = startingTile;
    }

    public Tile getTile() {
        return tile;
    }
}
