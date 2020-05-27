package it.polimi.ingsw.View.Communication;

import java.util.HashSet;
import java.util.Set;

public class LobbiesUpdateMessage extends Message {
    Set<LobbyInfo> lobbies = new HashSet<>();

    public LobbiesUpdateMessage(Set<LobbyInfo> lobbies) {
        this.lobbies.addAll(lobbies);
    }

    public Set<LobbyInfo> getLobbies() {
        return lobbies;
    }
}
