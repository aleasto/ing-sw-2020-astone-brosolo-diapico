package it.polimi.ingsw.View.Communication;

import java.util.HashSet;
import java.util.Set;

public class LobbiesUpdateMessage extends Message {
    Set<String> lobbyNames = new HashSet<>();

    public LobbiesUpdateMessage(Set<String> lobbyNames) {
        this.lobbyNames.addAll(lobbyNames);
    }

    public Set<String> getLobbyNames() {
        return lobbyNames;
    }
}
