package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

public class PlayerLoseEventMessage extends Message {
    private Player player;

    public PlayerLoseEventMessage(Player player) {
        Player tmpPlayer;
        try {
            tmpPlayer = player.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            // Fall back to using the actual object
            tmpPlayer = player;
        }
        this.player = tmpPlayer;
    }

    public Player getPlayer() {
        return player;
    }
}
