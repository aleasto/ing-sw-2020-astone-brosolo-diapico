package it.polimi.ingsw.View.Comunication;

import it.polimi.ingsw.Game.Player;

public class ConnectionMessage extends Message {
    private Player player;
    private String lobbyName;

    public ConnectionMessage(Player player, String lobbyName) {
        this.player = player;
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public Player getPlayer() {
        return player;
    }
}
