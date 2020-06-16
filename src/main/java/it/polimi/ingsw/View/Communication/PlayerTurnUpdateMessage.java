package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

public class PlayerTurnUpdateMessage extends Message {
    private final Player player;

    public PlayerTurnUpdateMessage(Player player) {
        Player tmpPlayer;
        try {
            tmpPlayer = player.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("Could not clone " + player + ": " + e.getMessage());
            System.out.println("Falling back to serializing shared object");
            tmpPlayer = player;
        }
        this.player = tmpPlayer;
    }

    public Player getPlayer() {
        return player;
    }
}
