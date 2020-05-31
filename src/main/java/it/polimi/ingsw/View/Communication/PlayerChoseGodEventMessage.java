package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Actions.GodInfo;
import it.polimi.ingsw.Game.Player;

public class PlayerChoseGodEventMessage extends Message {
    private final Player player;
    private final GodInfo god;

    public PlayerChoseGodEventMessage(Player player, GodInfo god) {
        this.player = player;
        this.god = god;
    }

    public Player getPlayer() {
        return player;
    }

    public GodInfo getGod() {
        return god;
    }
}
