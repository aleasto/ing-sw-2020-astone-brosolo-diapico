package it.polimi.ingsw.Game;

public class Board {
    private static final int DEFAULT_DIM_X = 5;
    private static final int DEFAULT_DIM_Y = 5;

    private int dimX;
    private int dimY;
    Tile[][] tileMatrix;

    public Board(int dimX, int dimY) {
        this.dimX = dimX;
        this.dimY = dimY;
        tileMatrix = new Tile[dimX][dimY];
    }

    public Board() {
        this(DEFAULT_DIM_X, DEFAULT_DIM_Y);
    }

    public Tile getAt(int x, int y) throws IndexOutOfBoundsException {
        if (x >= dimX || y >= dimY) {
            throw new IndexOutOfBoundsException("The specified position is outside of the board");
        }
        return tileMatrix[x][y];
    }
}
