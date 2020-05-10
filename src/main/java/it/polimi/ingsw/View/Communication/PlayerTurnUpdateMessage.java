package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

public class PlayerTurnUpdateMessage extends Message {
    private final Player player;

    public PlayerTurnUpdateMessage(Player player) {
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
