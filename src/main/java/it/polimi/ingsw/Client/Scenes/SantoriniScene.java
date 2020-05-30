package it.polimi.ingsw.Client.Scenes;

import javafx.scene.Scene;

public abstract class SantoriniScene {
    public abstract Scene getFXScene();

    public <T> T lookup(String id) {
        return (T) getFXScene().lookup(id);
    }

    protected static String SET_ID(String hashedId) {
        return hashedId.substring(1);
    }
}
