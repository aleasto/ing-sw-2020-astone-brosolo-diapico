package it.polimi.ingsw.Game;

public class Board {
    private static final int DEFAULT_DIM_X = 5;
    private static final int DEFAULT_DIM_Y = 5;

    private int dimX;
    private int dimY;
    private Tile[][] tileMatrix;

    public Board(int dimX, int dimY) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.tileMatrix = new Tile[dimX][dimY];
        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                this.tileMatrix[i][j] = new Tile(this, i, j);
            }
        }
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
