package it.polimi.ingsw.Utils;

import javafx.scene.control.Hyperlink;

public class TableLobbyInfo {
    private final Hyperlink name;
    private final int players;
    private final int spectators;
    private final String gameRunning;

    public TableLobbyInfo(Hyperlink name, int players, int spectators, String gameRunning) {
        this.name = name;
        this.players = players;
        this.spectators = spectators;
        this.gameRunning = gameRunning;
    }

    public Hyperlink getName() {
        return name;
    }

    public int getPlayers() {
        return players;
    }

    public int getSpectators() {
        return spectators;
    }

    public String getGameRunning() {
        return gameRunning;
    }
}
