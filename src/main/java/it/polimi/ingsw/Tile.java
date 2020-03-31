package it.polimi.ingsw;

public class Tile {
    private final static int MAX_HEIGHT = 4; // The fifth height would be on top of level 4 piece, aka the Dome
    private final static int WIN_HEIGHT = 3; // Player wins by standing on the fourth height, that is a piece of level 3

    private int height = 0;
    private int x, y;
    private Worker occupant;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public boolean buildUp() {
        if (getHeight() < MAX_HEIGHT) {
            height++;
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
        return height == MAX_HEIGHT;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
