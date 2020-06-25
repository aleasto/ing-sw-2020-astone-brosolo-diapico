package it.polimi.ingsw.Game;

import java.io.Serializable;

public class Tile implements Serializable, Cloneable {
    private int height = 0;
    private boolean dome = false;
    private final int x;
    private final int y;
    private Worker occupant;
    private Board board;

    /**
     * Create a tile object
     * @param board the board this tile is part of
     * @param x the x-coordinate this tile is placed at in the board
     * @param y the y-coordinate this tile is placed at in the board
     */
    public Tile(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
    }

    /**
     * Get the current height of this tile
     * @return the number of blocks that have been built on this tile.
     *         Example: a return value of 2 means that 2 blocks have been built, not counting a dome!
     */
    public int getHeight() {
        return height;
    }

    /** Builds a level on the given tile, effectively increasing its height by one, or building a dome if needed.
     * @return true if the tile was not at its max height and was not topped by a dome, false otherwise.
     */
    public boolean buildUp() {
        if (hasDome()) {
            return false;
        }
        if (getHeight() == board.getMaxHeight()) {
            return this.buildDome();
        }
        height++;
        board.onTileUpdate(this);
        return true;
    }

    /**
     * Builds a dome on top of the building
     * @return true if there wasn't a dome already.
     */
    public boolean buildDome() {
        if (!this.hasDome()) {
            dome = true;
            board.onTileUpdate(this);
            return true;
        }
        return false;
    }

    /**
     * @return true if a player would win by stepping up on this tile
     */
    public boolean isWinLevel() {
        return height == board.getMaxHeight();
    }

    /**
     * @return true if no worker is occupying this tile.
     */
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

    /**
     * Create a deep clone of this object
     * @return a new tile object that has the same height, dome, and occupant as this, but that is not placed on a board
     * @throws CloneNotSupportedException if any field is not cloneable
     */
    @Override
    public Tile clone() throws CloneNotSupportedException {
        Tile clone = (Tile) super.clone();
        clone.board = null;
        if (this.occupant != null) {
            clone.occupant = this.occupant.clone();
            clone.occupant.setTile(clone);
        }
        return clone;
    }

    /**
     * Set which board this tile is part of. Useful for board.clone()
     * @param board the board
     */
    public void setBoard(Board board) {
        this.board = board;
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
