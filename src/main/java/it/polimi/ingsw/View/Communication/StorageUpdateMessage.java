package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Storage;

public class StorageUpdateMessage extends Message {
    // TODO: Make this slimmer?
    private final Storage storage;

    public StorageUpdateMessage(Storage storage) {
        Storage tempStorage;
        try {
            tempStorage = storage.clone();
        } catch (CloneNotSupportedException e) {
            fail("Should not have thrown any exception");
        }
        this.storage = tempStorage;
    }

    public Storage getStorage() {
        return storage;
    }
}
