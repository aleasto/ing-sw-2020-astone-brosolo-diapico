package it.polimi.ingsw.View.Comunication;

import it.polimi.ingsw.Game.Player;

public class EndTurnCommandMessage extends Message {
    private Player player;

    public EndTurnCommandMessage(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
