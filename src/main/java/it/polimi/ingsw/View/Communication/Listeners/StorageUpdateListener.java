package it.polimi.ingsw.View.Communication.Listeners;

import it.polimi.ingsw.View.Communication.StorageUpdateMessage;

public interface StorageUpdateListener {
    void onStorageUpdate(StorageUpdateMessage message);
}
