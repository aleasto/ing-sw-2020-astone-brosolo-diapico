package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

public class PlayerTurnUpdateMessage extends Message {
    Player player;

    public PlayerTurnUpdateMessage(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
