package it.polimi.ingsw.Game;

import it.polimi.ingsw.Utils.ConfReader;
import it.polimi.ingsw.Utils.Pair;

import java.io.Serializable;
import java.util.Arrays;

public class GameRules implements Serializable {
    private Boolean playWithGods = null;
    private Pair<Integer, Integer> boardSize = null;
    private Integer[] blocks = null;
    private Integer workers = null;
    private Integer endGameTimer = null;

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

    public Integer[] getBlocks() {
        return blocks;
    }

    public void setBlocks(Integer ...blocks) {
        this.blocks = blocks;
    }

    public Integer getWorkers() {
        return workers;
    }

    public void setWorkers(Integer workers) {
        this.workers = workers;
    }

    public Integer getEndGameTimer() {
        return endGameTimer;
    }

    // Note: we do not simply initialize the variables to their defaults because we want *the server* to fill in
    // its defaults on rules we left as null.
    // These may differ from GUI defaults, and as such are repeated!
    private static final boolean DEFAULT_PLAY_WITH_GODS = true;
    private static final int DEFAULT_BOARD_SIZE_X = 5;
    private static final int DEFAULT_BOARD_SIZE_Y = 5;
    private static final Integer[] DEFAULT_BLOCKS = { 22, 18, 14, 14 };
    private static final int DEFAULT_WORKERS = 2;
    private static final int DEFAULT_END_GAME_TIMER = 10; // seconds

    /**
     * Fill game rules left to null by this object's creator with defaults from a conf reader.
     * Falls back to hard-coded values if the config parameter is not found
     * @param confReader the configuration reader associated with game rules.
     */
    public void fillDefaults(ConfReader confReader) {
        playWithGods = playWithGods != null ? playWithGods : confReader.getBoolean("play_with_gods", DEFAULT_PLAY_WITH_GODS);
        boardSize = boardSize != null ? boardSize : confReader.getIntPair("board_size", DEFAULT_BOARD_SIZE_X, DEFAULT_BOARD_SIZE_Y);
        blocks = blocks != null ? blocks : confReader.getInts("blocks", DEFAULT_BLOCKS);
        workers = workers != null ? workers : confReader.getInt("workers", DEFAULT_WORKERS);
        endGameTimer = endGameTimer != null ? endGameTimer : confReader.getInt("end_game_timer", DEFAULT_END_GAME_TIMER);
    }

    @Override
    public String toString() {
        return "GameRules{" +
                "playWithGods=" + playWithGods +
                ", boardSize=" + boardSize +
                ", blocks=" + Arrays.toString(blocks) +
                ", workers=" + workers +
                ", endGameTimer=" + endGameTimer +
                '}';
    }
}
