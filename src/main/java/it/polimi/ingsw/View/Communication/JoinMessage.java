package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

public class JoinMessage extends Message {
    private Player player;
    private String lobbyName;

    public JoinMessage(Player player, String lobbyName) {
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
