package it.polimi.ingsw.Game;

import it.polimi.ingsw.Game.Actions.Actions;

public class Player {
    private String name;
    private String godName;
    private Actions actions;
    private int godLikeLvl;

    public String getName() {
        return name;
    }

    public String getGodName() {
        return godName;
    }

    public int getGodLikeLvl() {
        return godLikeLvl;
    }

    public Actions getActions() {
        return actions;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }
}
