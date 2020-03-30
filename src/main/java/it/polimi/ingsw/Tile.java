package it.polimi.ingsw;

public class Tile {
    private final static int WIN_LEVEL = 2;
    private int height = 0;
    private int x, y;

    public int getHeight() {
        return height;
    }

    public boolean isTopLevel() {
        return height == WIN_LEVEL;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
