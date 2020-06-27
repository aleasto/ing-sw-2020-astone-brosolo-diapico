package it.polimi.ingsw.Game;

import it.polimi.ingsw.Game.Actions.Actions;

import java.io.Serializable;
import java.util.UUID;

public class Player implements Serializable, Cloneable {
    private final String name;
    private String godName;
    private transient Actions actions;
    private final int godLikeLvl;
    private final UUID uuid;

    /**
     * Create a player object
     * @param name the player name
     * @param godLikeLvl the player god-like level
     */
    public Player(String name, int godLikeLvl) {
        this.name = name;
        this.godLikeLvl = godLikeLvl;
        this.uuid = UUID.randomUUID();
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

    /**
     * Create a shallow clone of this player, but sets actions to null
     * @return a new player object that is equals() to this
     * @throws CloneNotSupportedException if any field is not cloneable
     */
    @Override
    public Player clone() throws CloneNotSupportedException {
        Player clone = (Player) super.clone();
        clone.actions = null;
        return clone;
    }

    /**
     * Is this player object equals to another one
     * @param obj the other player object
     * @return true if the other player has the same UUID as this.
     *         That is if and only if the other player object was created with clone()
     */
    @Override
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

    /**
     * A string representation of this player
     * @return a string containing the player's name and UUID
     */
    @Override
    public String toString() {
        return this.getName() + "[" + this.uuid + "]";
    }
}
