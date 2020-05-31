package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Actions.GodInfo;

import java.util.ArrayList;
import java.util.List;

public class GodListMessage extends Message {
    private final List<GodInfo> gods;
    private final int howManyToChoose;

    public GodListMessage(List<GodInfo> gods, int howManyToChoose) {
        if (gods == null) {
            this.gods = null;
        } else {
            this.gods = new ArrayList<>();
            this.gods.addAll(gods);
        }
        this.howManyToChoose = howManyToChoose;
    }

    public List<GodInfo> getGods() {
        return gods;
    }

    public int getHowManyToChoose() {
        return howManyToChoose;
    }
}
