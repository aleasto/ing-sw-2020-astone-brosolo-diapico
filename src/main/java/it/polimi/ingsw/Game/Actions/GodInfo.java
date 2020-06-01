package it.polimi.ingsw.Game.Actions;

import java.io.Serializable;

public class GodInfo implements Serializable {
    private final String name;
    private final String description;

    public GodInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GodInfo)) return false;
        GodInfo other = (GodInfo) o;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
