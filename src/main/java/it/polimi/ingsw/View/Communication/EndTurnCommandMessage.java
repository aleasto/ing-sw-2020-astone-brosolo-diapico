package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

public class EndTurnCommandMessage extends CommandMessage {
    private Player player;

    public EndTurnCommandMessage(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
