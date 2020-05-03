package it.polimi.ingsw.View.Communication;

import it.polimi.ingsw.Game.Storage;

public class StorageUpdateMessage extends Message {
    // TODO: Make this slimmer?
    private final Storage storage;

    public StorageUpdateMessage(Storage storage) {
        this.storage = storage;
    }

    public Storage getStorage() {
        return storage;
    }
}
