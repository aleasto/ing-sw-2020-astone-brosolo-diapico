package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Storage;

public class StorageUpdateMessage extends Message {
    private final Storage storage;

    public StorageUpdateMessage(Storage storage) {
        Storage tempStorage;
        try {
            tempStorage = storage.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("Could not clone " + storage + ": " + e.getMessage());
            System.out.println("Falling back to serializing shared object");
            tempStorage = null;
        }
        this.storage = tempStorage;
    }

    public Storage getStorage() {
        return storage;
    }
}
