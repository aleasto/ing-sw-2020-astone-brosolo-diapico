package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

public class PlayerChoseGodEventMessage extends Message {
    private final Player player;
    private final String god; //TODO: GodInfo

    public PlayerChoseGodEventMessage(Player player, String god) {
        this.player = player;
        this.god = god;
    }

    public Player getPlayer() {
        return player;
    }

    public String getGod() {
        return god;
    }
}
