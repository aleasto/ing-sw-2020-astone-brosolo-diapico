package it.polimi.ingsw.Game;

import it.polimi.ingsw.Utils.ConfReader;
import it.polimi.ingsw.Utils.Pair;

import java.io.Serializable;

public class GameRules implements Serializable {
    private Boolean playWithGods = null;
    private Pair<Integer, Integer> boardSize = null;

    public Boolean getPlayWithGods() {
        return playWithGods;
    }

    public void setPlayWithGods(Boolean playWithGods) {
        this.playWithGods = playWithGods;
    }

    public Pair<Integer, Integer> getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(Pair<Integer, Integer> size) {
        this.boardSize = size;
    }

    public void fillDefaults(ConfReader confReader) {
        playWithGods = playWithGods != null ? playWithGods : confReader.getBoolean("play_with_gods", true);
        boardSize = boardSize != null ? boardSize : confReader.getIntPair("board_size", 5, 5);
    }
}
