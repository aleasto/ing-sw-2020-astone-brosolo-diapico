package it.polimi.ingsw.Game;

import it.polimi.ingsw.View.Communication.BoardUpdateMessage;
import it.polimi.ingsw.View.Communication.Broadcasters.BoardUpdateBroadcaster;
import it.polimi.ingsw.View.Communication.Listeners.BoardUpdateListener;
import it.polimi.ingsw.View.Communication.Listeners.TileUpdateListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board implements BoardUpdateBroadcaster, TileUpdateListener, Serializable, Cloneable {
    private final int dimX;
    private final int dimY;
    private final int maxHeight;
    private Tile[][] tileMatrix;

    public Board(int dimX, int dimY, int maxHeight) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.maxHeight = maxHeight;
        this.tileMatrix = new Tile[dimX][dimY];
        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                this.tileMatrix[i][j] = new Tile(this, i, j);
            }
        }
    }

    public Tile getAt(int x, int y) throws IndexOutOfBoundsException {
        if (x >= dimX || y >= dimY) {
            throw new IndexOutOfBoundsException("The specified position is outside of the board");
        }
        return tileMatrix[x][y];
    }

    public int getDimX() {
        return dimX;
    }

    public int getDimY() {
        return dimY;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    @Override
    public Board clone() throws CloneNotSupportedException {
        Board clone = (Board) super.clone();
        clone.tileMatrix = new Tile[dimX][dimY];
        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                clone.tileMatrix[i][j] = this.tileMatrix[i][j].clone();
                clone.tileMatrix[i][j].setBoard(clone);
            }
        }
        return clone;
    }

    @Override
    public void onTileUpdate(Tile message) {
        notifyBoardUpdate(new BoardUpdateMessage(this));
    }

    final transient List<BoardUpdateListener> boardUpdateListeners = new ArrayList<>();
    @Override
    public void addBoardUpdateListener(BoardUpdateListener listener){
        synchronized (boardUpdateListeners) {
            boardUpdateListeners.add(listener);
        }
        onRegisterForBoardUpdate(listener);
    }
    @Override
    public void removeBoardUpdateListener(BoardUpdateListener listener){
        synchronized (boardUpdateListeners) {
            boardUpdateListeners.remove(listener);
        }
    }
    @Override
    public void notifyBoardUpdate(BoardUpdateMessage message) {
        synchronized (boardUpdateListeners) {
            for (BoardUpdateListener listener : boardUpdateListeners) {
                listener.onBoardUpdate(message);
            }
        }
    }
    @Override
    public void onRegisterForBoardUpdate(BoardUpdateListener listener) {
        // Send initial data to the newly registered listener
        listener.onBoardUpdate(new BoardUpdateMessage(this));
    }

}
