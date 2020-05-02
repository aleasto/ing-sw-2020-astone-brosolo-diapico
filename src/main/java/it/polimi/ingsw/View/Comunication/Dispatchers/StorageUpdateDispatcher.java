package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.StorageUpdateMessage;
import it.polimi.ingsw.View.Comunication.Listeners.StorageUpdateListener;

public interface StorageUpdateDispatcher {
    void addStorageUpdateListener(StorageUpdateListener listener);
    void removeStorageUpdateListener(StorageUpdateListener listener);
    void notifyStorageUpdate(StorageUpdateMessage message);
    void onRegisterForStorageUpdate(StorageUpdateListener listener);
}
