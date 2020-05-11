package it.polimi.ingsw.View.Communication;

import java.util.ArrayList;
import java.util.List;

public class GodListMessage extends Message {
    private final List<String> gods;

    public GodListMessage(List<String> gods) {
        if (gods == null) {
            this.gods = null;
        } else {
            this.gods = new ArrayList<>();
            this.gods.addAll(gods);
        }
    }

    public List<String> getGods() {
        return gods;
    }
}
