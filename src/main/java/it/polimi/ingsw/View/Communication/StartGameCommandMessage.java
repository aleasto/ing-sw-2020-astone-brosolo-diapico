package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

public class StartGameCommandMessage extends Message {
    private Player player;

    public StartGameCommandMessage(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
