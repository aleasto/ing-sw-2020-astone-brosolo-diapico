package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Actions.GodInfo;
import it.polimi.ingsw.Game.Player;

public class PlayerChoseGodEventMessage extends Message {
    private final Player player;
    private final GodInfo god;

    public PlayerChoseGodEventMessage(Player player, GodInfo god) {
        Player tmpPlayer;
        try {
            tmpPlayer = player.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("Could not clone " + player + ": " + e.getMessage());
            System.out.println("Falling back to serializing shared object");
            tmpPlayer = player;
        }
        this.player = tmpPlayer;
        this.god = god;
    }

    public Player getPlayer() {
        return player;
    }

    public GodInfo getGod() {
        return god;
    }
}
