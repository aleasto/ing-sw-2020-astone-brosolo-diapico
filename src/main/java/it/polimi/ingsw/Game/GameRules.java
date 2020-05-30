package it.polimi.ingsw.Game;

import it.polimi.ingsw.Utils.ConfReader;

import java.io.Serializable;

public class GameRules implements Serializable {
    private Boolean playWithGods = null;

    public Boolean getPlayWithGods() {
        return playWithGods;
    }

    public void setPlayWithGods(Boolean playWithGods) {
        this.playWithGods = playWithGods;
    }

    public void fillDefaults(ConfReader confReader) {
        playWithGods = playWithGods != null ? playWithGods : confReader.getBoolean("play_with_gods", true);
    }
}
