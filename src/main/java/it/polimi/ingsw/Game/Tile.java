package it.polimi.ingsw.Game;

import java.io.Serializable;

public class Tile implements Serializable, Cloneable {
    private final static int MAX_HEIGHT = 3; // Player wins by standing on the fourth height, that is a piece of level 3

    private int height = 0;
    private boolean dome = false;
    private int x, y;
    private Worker occupant;
    private Board board;

    public Tile(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    // Builds a level on the given tile, effectively increasing its height by one, or building a dome if needed.
    public boolean buildUp() {
        if (hasDome()) {
            return false;
        }
        if (getHeight() == getMaxHeight()) {
            return this.buildDome();
        }
        height++;
        board.onTileUpdate(this);
        return true;
    }

    //Builds a dome on top of the building
    public boolean buildDome() {
        if (!this.hasDome()) {
            dome = true;
            board.onTileUpdate(this);
            return true;
        }
        return false;
    }

    public boolean isWinLevel() {
        return height == MAX_HEIGHT;
    }

    public boolean isEmpty() {
        return occupant == null;
    }

    public boolean hasDome() {
        return dome;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static int getMaxHeight() {
        return MAX_HEIGHT;
    }

    public Worker getOccupant() {
        return occupant;
    }

    public Board getBoard() {
        return board;
    }

    public void setOccupant(Worker occupant) {
        this.occupant = occupant;
        board.onTileUpdate(this);
    }

    @Override
    public Tile clone() throws CloneNotSupportedException {
        Tile clone = (Tile) super.clone();
        clone.height = this.height;
        clone.dome = this.dome;
        clone.x = this.x;
        clone.y = this.y;
        clone.board = null;
        clone.occupant = this.occupant.clone();
        return clone;
    }

    /**
     * Two tiles are to be considered the same if they have the same X and Y
     *
     * @param o A tile to be confronted with this
     * @return a boolean indicating if the tiles have the same coordinates
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tile)) return false;
        Tile tile = (Tile) o;
        return x == tile.x &&
                y == tile.y;
    }
}
