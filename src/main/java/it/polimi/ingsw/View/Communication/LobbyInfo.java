package it.polimi.ingsw.View.Communication;


import java.io.Serializable;

public class LobbyInfo implements Serializable {
    private final String name;
    private final int players;
    private final int spectators;
    private final boolean gameRunning;

    public LobbyInfo(String name, int players, int spectators, boolean gameRunning) {
        this.name = name;
        this.players = players;
        this.spectators = spectators;
        this.gameRunning = gameRunning;
    }

    public String getName() {
        return name;
    }

    public int getPlayers() {
        return players;
    }

    public int getSpectators() {
        return spectators;
    }

    public boolean getGameRunning() {
        return gameRunning;
    }
}
