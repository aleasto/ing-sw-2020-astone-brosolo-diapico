package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayersUpdateMessage extends Message {
    private final List<Player> playerList = new ArrayList<>();
    private final List<Player> spectatorList = new ArrayList<>();

    public PlayersUpdateMessage(List<Player> playerList, List<Player> spectatorList) {
        for (Player p : playerList) {
            try {
                this.playerList.add(p.clone());
            } catch (CloneNotSupportedException e) {
                System.out.println("Could not clone " + p + ": " + e.getMessage());
                System.out.println("Falling back to serializing shared object");
                this.playerList.add(p);
            }
        }

        for (Player p : spectatorList) {
            try {
                this.spectatorList.add(p.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                // Fall back to adding the actual object
                this.spectatorList.add(p);
            }
        }
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public List<Player> getSpectatorList() {
        return spectatorList;
    }
}
