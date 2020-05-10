package it.polimi.ingsw.View.Communication;

import java.util.ArrayList;
import java.util.List;

public class GodListMessage extends Message {
    private final List<String> gods = new ArrayList<>();

    public GodListMessage(List<String> gods) {
        this.gods.addAll(gods);
    }

    public List<String> getGods() {
        return gods;
    }
}
