package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

public class JoinCommandMessage extends CommandMessage {
    private final Player player;
    private final String lobbyName;

    public JoinCommandMessage(Player player, String lobbyName) {
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
