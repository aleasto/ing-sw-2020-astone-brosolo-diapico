package it.polimi.ingsw.Game;

import it.polimi.ingsw.Game.Actions.Actions;

import java.io.Serializable;

public class Player implements Serializable {
    private String name;
    private String godName;
    private transient Actions actions;
    private int godLikeLvl;

    public Player(String name, String godName, int godLikeLvl) {
        this.name = name;
        this.godName = godName;
        this.godLikeLvl = godLikeLvl;
    }

    public Player() {
        this("foo", null, 0);
    }

    public String getName() {
        return name;
    }

    public String getGodName() {
        return godName;
    }

    public int getGodLikeLevel() {
        return godLikeLvl;
    }

    public Actions getActions() {
        return actions;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }
}
