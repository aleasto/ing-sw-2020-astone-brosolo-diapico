package it.polimi.ingsw.View.Communication.Dispatchers;

import it.polimi.ingsw.View.Communication.StorageUpdateMessage;
import it.polimi.ingsw.View.Communication.Listeners.StorageUpdateListener;

public interface StorageUpdateDispatcher {
    void addStorageUpdateListener(StorageUpdateListener listener);
    void removeStorageUpdateListener(StorageUpdateListener listener);
    void notifyStorageUpdate(StorageUpdateMessage message);
    void onRegisterForStorageUpdate(StorageUpdateListener listener);
}
