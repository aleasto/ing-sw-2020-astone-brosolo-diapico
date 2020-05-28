package it.polimi.ingsw.Game;

import it.polimi.ingsw.Game.Actions.Actions;

import java.io.Serializable;
import java.util.UUID;

public class Player implements Serializable, Cloneable {
    private final String name;
    private transient String godName;
    private transient Actions actions;
    private final int godLikeLvl;
    private final UUID uuid;

    public Player(String name, String godName, int godLikeLvl) {
        this.name = name;
        this.godName = godName;
        this.godLikeLvl = godLikeLvl;
        this.uuid = UUID.randomUUID();
    }

    public Player(String name, int godLikeLvl) {
        this(name, null, godLikeLvl);
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

    public void setGodName(String godName) {
        this.godName = godName;
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

    @Override
    public Player clone() throws CloneNotSupportedException {
        Player clone = (Player) super.clone();
        clone.actions = null;
        return clone;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Player)) return false;
        Player other = (Player) obj;
        return this.uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }
}
