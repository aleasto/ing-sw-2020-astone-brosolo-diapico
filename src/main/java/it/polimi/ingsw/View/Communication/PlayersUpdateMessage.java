package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

import java.util.List;

public class PlayersUpdateMessage extends Message {
    List<Player> playerList;

    public PlayersUpdateMessage(List<Player> playerList) {
        this.playerList = playerList;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }
}
