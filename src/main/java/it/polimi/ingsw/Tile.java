package it.polimi.ingsw;

public class Tile {
    private final static int MAX_HEIGHT = 4; // The fifth height would be on top of level 4 piece, aka the Dome
    private final static int WIN_HEIGHT = 3; // Player wins by standing on the fourth height, that is a piece of level 3

    private int height = 0;
    private boolean dome = false;
    private int x, y;
    private Worker occupant;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    // Builds a level on the given tile, effectively increasing its height by one, or building a dome if needed.
    public boolean buildUp() {
        if (getHeight() < MAX_HEIGHT && !hasDome()) {
            if (getHeight() == WIN_HEIGHT) {
                return this.buildDome();
            }
            height++;
            return true;
        }
        return false;
    }

    //Builds a dome on top of the building
    public boolean buildDome() {
        if (!this.hasDome()) {
            dome = true;
            return true;
        }
        return false;
    }

    public boolean isWinLevel() {
        return height == WIN_HEIGHT;
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

    public int getMaxHeight() {
        return MAX_HEIGHT;
    }

    public int getWinHeight() {
        return WIN_HEIGHT;
    }

    public Worker getOccupant() {
        return occupant;
    }

    public void setOccupant(Worker occupant) {
        this.occupant = occupant;
    }

    /**
     * Due tiles are to be considered the same if they have the same X and Y
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
