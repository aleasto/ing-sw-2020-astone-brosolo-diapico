package it.polimi.ingsw.View.Communication;

import java.util.List;

public class GodListMessage extends Message {
    List<String> gods;

    public GodListMessage(List<String> gods) {
        this.gods = gods;
    }

    public List<String> getGods() {
        return gods;
    }
}
