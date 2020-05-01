package it.polimi.ingsw.View.Comunication.Listeners;

import it.polimi.ingsw.View.Comunication.StorageUpdateMessage;

public interface StorageUpdateListener {
    void onStorageUpdate(StorageUpdateMessage message);
}
