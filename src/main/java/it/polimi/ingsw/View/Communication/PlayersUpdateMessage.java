package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayersUpdateMessage extends Message {
    private final List<Player> playerList = new ArrayList<>();

    public PlayersUpdateMessage(List<Player> playerList) {
        for (Player p : playerList) {
            try {
                this.playerList.add(p.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                // Fall back to adding the actual object
                this.playerList.add(p);
            }
        }
    }

    public List<Player> getPlayerList() {
        return playerList;
    }
}
