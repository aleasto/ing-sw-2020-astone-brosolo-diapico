package it.polimi.ingsw.View.Communication;

import java.util.ArrayList;
import java.util.List;

public class GodListMessage extends Message {
    private final List<String> gods;
    private final int howManyToChoose;

    public GodListMessage(List<String> gods, int howManyToChoose) {
        if (gods == null) {
            this.gods = null;
        } else {
            this.gods = new ArrayList<>();
            this.gods.addAll(gods);
        }
        this.howManyToChoose = howManyToChoose;
    }

    public List<String> getGods() {
        return gods;
    }

    public int getHowManyToChoose() {
        return howManyToChoose;
    }
}
